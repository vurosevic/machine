package com.vurosevic.dasis.nn.lstmnet.service.impl;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.nn.lstmnet.LstmSeq2Seq;
import com.vurosevic.dasis.nn.lstmnet.service.ExperimentService;
import com.vurosevic.dasis.nn.lstmnet.LstmNet;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private DataPreparationService dataPreparationService;

    @Autowired
    private LstmNet lstmNet;

    @Autowired
    private LstmSeq2Seq lstmSeq2Seq;

    @Override
    public boolean runExperimentS2S(ExperimentDto experimentDto, ConfigRecord configRecord) {
        lstmSeq2Seq.setConfig(configRecord);
        lstmSeq2Seq.setExperimentDto(experimentDto);

        dataPreparationService.makeCsvFiles(experimentDto);
        log.info("CSV files are created.");

        lstmSeq2Seq.initNetwork();
        log.info("LstmSeq2Seq is initialized.");

        lstmSeq2Seq.loadData();
        log.info("Data is loaded.");

        lstmSeq2Seq.trainNetwork();
        log.info("LstmSeq2Seq is trained." );

        lstmSeq2Seq.loadLastState();

        log.info("AvgMape for testDataset is: {}", lstmSeq2Seq.calculateTestAvgMape());

        return true;
    }

    @Override
    public boolean runExperiment(ExperimentDto experimentDto, ConfigRecord configRecord) {

        lstmNet.setConfig(configRecord);
        lstmNet.setExperimentDto(experimentDto);

        dataPreparationService.makeCsvFiles(experimentDto);
        log.info("CSV files are created.");

        lstmNet.initNetwork();
        log.info("LSTM is initialized.");

        lstmNet.loadData();
        log.info("Data is loaded.");

        lstmNet.trainNetwork();
        log.info("LSTM is trained." );

        lstmNet.loadLastState();

        log.info("AvgMape for testDataset is: {}", lstmNet.calculateTestAvgMape());

        return true;
    }
}

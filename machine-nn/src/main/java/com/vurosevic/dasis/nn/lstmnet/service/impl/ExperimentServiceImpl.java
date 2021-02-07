package com.vurosevic.dasis.nn.lstmnet.service.impl;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.nn.lstmnet.LstmNet;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import com.vurosevic.dasis.nn.lstmnet.service.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private DataPreparationService dataPreparationService;

    @Autowired
    @Qualifier("LstmNetImpl")
    private LstmNet lstmNet;

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

package com.vurosevic.dasis.server.app;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.nn.lstmnet.service.ExperimentService;
import com.vurosevic.dasis.nn.lstmnet.LstmNet;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import com.vurosevic.dasis.persistence.model.Experiment;
import com.vurosevic.dasis.persistence.repository.ExperimentRepository;
import com.vurosevic.dasis.persistence.repository.LoadEntsoeRepository;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@SpringBootApplication(scanBasePackages={"com.vurosevic.dasis.persistence.repository.*",
        "com.vurosevic.dasis.persistence.model.*",
        "com.vurosevic.dasis.app.*",
        "com.vurosevic.dasis.app.service.*",
        "com.vurosevic.dasis.*"})
@EnableJpaRepositories("com.vurosevic.dasis.persistence.repository")
@EntityScan("com.vurosevic.dasis.persistence.model")
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    LoadEntsoeRepository loadEntsoeRepository;

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    DataPreparationService dataPreparationService;

    @Autowired
    LstmNet lstmNet;

    @Autowired
    ExperimentService experimentService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("RUNNING EXPERIMENTS");

        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        MapperFacade orikaMapperFacade = mapperFactory.getMapperFacade();

        List<Experiment> experiments = experimentRepository.findByProjectId(10L); // 10 - 15
        List<ExperimentDto> experimentDtos = orikaMapperFacade.mapAsList(experiments, ExperimentDto.class);

        for (ExperimentDto experimentDto : experimentDtos) {
            log.info("------------------------------------------------------------");

            if (experimentDto.getId() >= 392) {

                ConfigRecord configRecord = ConfigRecord.builder()
                        .batchSize(1)
                        .learningRate(0.0015)
                        .epoch(1000)
                        .modelFileName(experimentDto.getName() + "_" + experimentDto.getId() + ".lstm")
                        .build();

                log.info("MODEL START : {}_{}",experimentDto.getModel().getNameModel(), experimentDto.getId());
                experimentService.runExperiment(experimentDto, configRecord);
                log.info("MODEL DONE : {}_{}",experimentDto.getModel().getNameModel(), experimentDto.getId());

            }

            log.info("------------------------------------------------------------");
        }

//        lstmNet.setConfig(configRecord);
//        lstmNet.setExperimentDto(experimentDtos.get(26));
//
//        dataPreparationService.makeCsvFiles(experimentDtos.get(26));
//        log.info("CSV files are created.");
//
////        lstmNet.initNetwork();
////        log.info("LSTM is initialized.");
////
////        lstmNet.loadData();
////        log.info("Data is loaded.");
////
////        lstmNet.trainNetwork();
////        log.info("LSTM is trained." );
//
////        lstmNet.load("D:\\machine\\models\\CZ_MODEL_27.lstm");
////        log.info("Model is loaded." );
////
//        lstmNet.initNetwork();
//        log.info("LSTM is initialized.");
//
//        lstmNet.loadData();
//        log.info("Data is loaded.");
//
//        lstmNet.loadLastState();
//
//        //Double mape = lstmNet.calculateAvgMape();
//
//        log.info("AvgMape for testDataset is: {}", lstmNet.calculateTestAvgMape());

        log.info("END.");
    }

}

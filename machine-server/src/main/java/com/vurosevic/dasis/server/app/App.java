package com.vurosevic.dasis.server.app;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import com.vurosevic.dasis.nn.lstmnet.service.ExperimentService;
import com.vurosevic.dasis.persistence.model.Experiment;
import com.vurosevic.dasis.persistence.repository.ExperimentRepository;
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
    private ExperimentRepository experimentRepository;

    @Autowired
    private ExperimentService experimentService;

    private static final Long PROJECT_ID = 27L;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {

        log.info("*******************************************");
        log.info("* Short-Term forecast using LSTM Networks *");
        log.info("* by Sliding window method                *");
        log.info("*-----------------------------------------*");
        log.info("* Phd student Vladimir Urosevic           *");
        log.info("*******************************************");

        log.info("RUNNING PROJECT - " + PROJECT_ID);

        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        MapperFacade orikaMapperFacade = mapperFactory.getMapperFacade();

        List<Experiment> experiments = experimentRepository.findByProjectId(PROJECT_ID);
        List<ExperimentDto> experimentDtos = orikaMapperFacade.mapAsList(experiments, ExperimentDto.class);

        for (ExperimentDto experimentDto : experimentDtos) {
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

        log.info("END.");
    }

}

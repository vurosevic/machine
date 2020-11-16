package com.vurosevic.dasis.app.app;

import com.vurosevic.dasis.app.dto.CountryDto;
import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.dto.ModelDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.persistence.model.Experiment;
import com.vurosevic.dasis.persistence.repository.ExperimentRepository;
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
//@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    DataPreparationService dataPreparationService;

    @Autowired
    ExperimentRepository experimentRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("TEST");

        List<Experiment> experiments = experimentRepository.findAll();


      //  dataPreparationService.makeCsvFiles(experiments.get(0));

    }
}

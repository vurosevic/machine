package com.vurosevic.dasis.app.service.impl;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.ExperimentResultService;
import com.vurosevic.dasis.persistence.model.Experiment;
import com.vurosevic.dasis.persistence.model.ExperimentResultsTest;
import com.vurosevic.dasis.persistence.model.ExperimentResultsTraining;
import com.vurosevic.dasis.persistence.model.ExperimentResultsValidation;
import com.vurosevic.dasis.persistence.repository.ExperimentResultsTestRepository;
import com.vurosevic.dasis.persistence.repository.ExperimentResultsTrainingRepository;
import com.vurosevic.dasis.persistence.repository.ExperimentResultsValidationRepository;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExperimentResultServiceImpl implements ExperimentResultService {

    private final ExperimentResultsTrainingRepository experimentResultsTrainingRepository;
    private final ExperimentResultsValidationRepository experimentResultsValidationRepository;
    private final ExperimentResultsTestRepository experimentResultsTestRepository;
    private final MapperFacade orikaMapperFacade;

    @Override
    public void saveTrainingResult(ExperimentDto experimentDto, Integer epoch, Double mape) {

        ExperimentResultsTraining experimentResultsTraining = ExperimentResultsTraining.builder()
                .id(0L)
                .experiment(orikaMapperFacade.map(experimentDto, Experiment.class))
                .epoch(epoch)
                .mape(mape)
                .build();

        experimentResultsTrainingRepository.save(experimentResultsTraining);
    }

    @Override
    public void saveValidationResult(ExperimentDto experimentDto, Integer epoch, Double mape) {

        ExperimentResultsValidation experimentResultsValidation = ExperimentResultsValidation.builder()
                .id(0L)
                .experiment(orikaMapperFacade.map(experimentDto, Experiment.class))
                .epoch(epoch)
                .mape(mape)
                .build();

        experimentResultsValidationRepository.save(experimentResultsValidation);
    }

    @Override
    public void saveTestResult(ExperimentDto experimentDto, Integer epoch, Double mape) {

        ExperimentResultsTest experimentResultsTest = ExperimentResultsTest.builder()
                .id(0L)
                .experiment(orikaMapperFacade.map(experimentDto, Experiment.class))
                .epoch(epoch)
                .mape(mape)
                .build();

        experimentResultsTestRepository.save(experimentResultsTest);
    }
}

package com.vurosevic.dasis.app.service.impl;

import com.vurosevic.dasis.app.dto.CrossValidationResultDto;
import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.CrossValidationService;
import com.vurosevic.dasis.persistence.model.CrossValidationDetail;
import com.vurosevic.dasis.persistence.model.CrossValidationResult;
import com.vurosevic.dasis.persistence.model.Experiment;
import com.vurosevic.dasis.persistence.repository.CrossValidationDetailsRepository;
import com.vurosevic.dasis.persistence.repository.CrossValidationResultRepository;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CrossValidationServiceImpl implements CrossValidationService {

    private final CrossValidationResultRepository crossValidationResultRepository;
    private final CrossValidationDetailsRepository crossValidationDetailsRepository;
    private final MapperFacade orikaMapperFacade;

    @Override
    public CrossValidationResultDto saveCrossValidationResult(ExperimentDto experimentDto, Integer ordinalNumber) {
        CrossValidationResult result = CrossValidationResult.builder()
                .id(0L)
                .experiment(orikaMapperFacade.map(experimentDto, Experiment.class))
                .ordinalNumber(ordinalNumber)
                .build();
        return orikaMapperFacade.map(crossValidationResultRepository.save(result), CrossValidationResultDto.class);
    }

    @Override
    public void saveCrossValidationDetail(CrossValidationResultDto crossValidationResultDto, Integer ordinalNumber, Double mape, Double label, Double realValue) {
        CrossValidationDetail result = CrossValidationDetail.builder()
                .id(0L)
                .crossValidationResult(orikaMapperFacade.map(crossValidationResultDto, CrossValidationResult.class))
                .ordinalNumber(ordinalNumber)
                .mape(mape)
                .label(label)
                .realValue(realValue)
                .build();
        crossValidationDetailsRepository.save(result);
    }

}

package com.vurosevic.dasis.app.service;

import com.vurosevic.dasis.app.dto.CrossValidationResultDto;
import com.vurosevic.dasis.app.dto.ExperimentDto;

public interface CrossValidationService {

    CrossValidationResultDto saveCrossValidationResult(ExperimentDto experimentDto, Integer ordinalNumber);
    void saveCrossValidationDetail(CrossValidationResultDto crossValidationResultDto, Integer ordinalNumber, Double mape, Double label, Double realValue);

}

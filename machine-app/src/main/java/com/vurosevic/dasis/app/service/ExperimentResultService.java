package com.vurosevic.dasis.app.service;

import com.vurosevic.dasis.app.dto.ExperimentDto;

public interface ExperimentResultService {

    void saveTrainingResult(ExperimentDto experimentDto, Integer epoch, Double mape);
    void saveValidationResult(ExperimentDto experimentDto, Integer epoch, Double mape);
    void saveTestResult(ExperimentDto experimentDto, Integer epoch, Double mape);

}

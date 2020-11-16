package com.vurosevic.dasis.app.service;

import com.vurosevic.dasis.app.dto.ExperimentDto;

public interface DataPreparationService {

    String getCsvFileNameAllDataSet(ExperimentDto experiment);
    String getCsvFileNameTrainigDataSet(ExperimentDto experiment, String prefix);
    String getCsvFileNameValidationDataSet(ExperimentDto experiment, String prefix);
    String getCsvFileNameTestDataSet(ExperimentDto experiment, String prefix);

    void makeCsvFiles(ExperimentDto experiment);
}

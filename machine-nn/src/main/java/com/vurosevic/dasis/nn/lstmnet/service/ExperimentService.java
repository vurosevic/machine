package com.vurosevic.dasis.nn.lstmnet.service;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;

public interface ExperimentService {

    boolean runExperiment(ExperimentDto experimentDto, ConfigRecord configRecord);
    boolean runExperimentS2S(ExperimentDto experimentDto, ConfigRecord configRecord);

}

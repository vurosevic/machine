package com.vurosevic.dasis.nn.lstmnet;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;

import java.io.IOException;

public interface LstmNet {

    ExperimentDto getExperimentDto();
    void setExperimentDto(ExperimentDto experimentDto);
    void initNetwork();
    void trainNetwork();
    double[] predict(double[] inputArray);
    Boolean loadData();
    void loadLastState();
    void load(String filename) throws IOException;
    void save(String filename);
    Integer getEpoch();
    void setEpoch(int epoch);
    void setConfig(ConfigRecord configRecord);
    Double calculateTestAvgMape();

}

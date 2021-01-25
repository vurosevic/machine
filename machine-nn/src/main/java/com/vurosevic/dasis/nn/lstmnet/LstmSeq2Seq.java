package com.vurosevic.dasis.nn.lstmnet;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;

import java.io.IOException;

public interface LstmSeq2Seq {

    ExperimentDto getExperimentDto();
    void setExperimentDto(ExperimentDto experimentDto);
    ComputationGraphConfiguration getLstmNetworkConfiguration();
    void initNetwork();
    void trainNetwork();
    void trainNetworkEarlyStopping();
    double[] predict(double[] inputArray);
    double getMape(DataSet testData, INDArray predicted, DataNormalization normalizer);
    double getCurrentMape();
    boolean loadData();
    void loadLastState();
    void load(String filename) throws IOException;
    void save(String filename);
    int getEpoch();
    void setEpoch(int epoch);
    Double calculateAvgMape();
    void trainNetwork(int epoch);
    void setConfig(ConfigRecord configRecord);
    Double calculateTestAvgMape();

}

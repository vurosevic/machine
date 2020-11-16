package com.vurosevic.dasis.nn.lstmnet.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRecord {

    private double learningRate;
    private int epoch;
    private String modelFileName;
    private int batchSize;

}

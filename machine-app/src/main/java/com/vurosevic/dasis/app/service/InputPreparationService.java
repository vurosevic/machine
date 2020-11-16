package com.vurosevic.dasis.app.service;

public interface InputPreparationService {

    void clearBuffer();
    int getLength();
    void setLength(int length);
    void push(Double newValue);
    double[] getInputData();

}

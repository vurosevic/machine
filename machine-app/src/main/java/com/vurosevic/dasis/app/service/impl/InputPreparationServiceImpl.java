package com.vurosevic.dasis.app.service.impl;

import com.vurosevic.dasis.app.service.InputPreparationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InputPreparationServiceImpl implements InputPreparationService {

    private int length;
    private List<Double> buffer;

    public InputPreparationServiceImpl() {
        length = 0;
        buffer = new ArrayList<>();
    }

    @Override
    public void clearBuffer() {
        buffer.clear();
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }

    public void push(Double newValue) {
        buffer.add(newValue);
    }

    public double[] getInputData() {
        double[] result = new double[length];
        for (int i = 0; i< length; i++)
            result[i] = buffer.get(buffer.size()- length +i);
        return result;
    }
}

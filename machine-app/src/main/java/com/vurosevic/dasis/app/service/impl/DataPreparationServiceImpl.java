package com.vurosevic.dasis.app.service.impl;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.persistence.model.LoadEntsoe;
import com.vurosevic.dasis.persistence.repository.LoadEntsoeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vurosevic.dasis.app.enums.PrefixCsv.FEATURE;
import static com.vurosevic.dasis.app.enums.PrefixCsv.LABEL;

@Service
public class DataPreparationServiceImpl implements DataPreparationService {

    private static final Double TRAINING_DATA_SET_PERCENT = 0.80; //0.60
    private static final Double VALIDATION_DATA_SET_PERCENT = 0.10; // 0.20

    private static final String START_PATH = "D:\\machine\\data\\";

    @Autowired
    private LoadEntsoeRepository loadEntsoeRepository;

    @Override
    public String getCsvFileNameAllDataSet(ExperimentDto experiment) {
        return START_PATH + experiment.getProject().getName()
                + "-" + experiment.getCountry().getShortcut()
                + "-" + experiment.getModel().getNameModel()
                + "-FULL-DATA-SET"
                + "-" + experiment.getNumInputs()
                + "-" + experiment.getNumOutputs()
                + "-" + experiment.getNumHour()
                + "_0.CSV";
    }

    @Override
    public String getCsvFileNameTrainigDataSet(ExperimentDto experiment, String prefix) {
        return START_PATH + prefix
                //+ experiment.getProject().getName()
                + experiment.getCountry().getShortcut()
                //+ "-" + experiment.getModel().getNameModel()
                + "TrainingDataSet"
                + "-" + experiment.getNumInputs()
                + "-" + experiment.getNumOutputs()
                + "-" + experiment.getNumHour()
                + "_0.csv";
    }

    @Override
    public String getCsvFileNameValidationDataSet(ExperimentDto experiment, String prefix) {
        return START_PATH + prefix
                //+ experiment.getProject().getName()
                + experiment.getCountry().getShortcut()
                //+ "-" + experiment.getModel().getNameModel()
                + "ValidationDataSet"
                + "-" + experiment.getNumInputs()
                + "-" + experiment.getNumOutputs()
                + "-" + experiment.getNumHour()
                + "_0.csv";
    }

    @Override
    public String getCsvFileNameTestDataSet(ExperimentDto experiment, String prefix) {
        return START_PATH + prefix
                //+ experiment.getProject().getName()
                + experiment.getCountry().getShortcut()
                //+ "-" + experiment.getModel().getNameModel()
                + "TestDataSet"
                + "-" + experiment.getNumInputs()
                + "-" + experiment.getNumOutputs()
                + "-" + experiment.getNumHour()
                + "_0.csv";
    }

    @Override
    public void makeCsvFiles(ExperimentDto experiment) {

        List<LoadEntsoe> allData = loadEntsoeRepository.findByCountryIdOrderByLoadDateAscLoadHourAscLoadMinuteAsc(experiment.getCountry().getId());

        List<String> allLines = new ArrayList<>();
        List<String> featuredLines = new ArrayList<>();
        List<String> labeledLines = new ArrayList<>();

        allData.forEach(loadEntsoe -> allLines.add(loadEntsoe.getLoadReal().toString()));

        for (int i=0; i<allData.size()-experiment.getNumInputs()-experiment.getNumOutputs(); i++) {
            featuredLines.add(makeFeaturedLine(experiment, allData, i));
            labeledLines.add(makeLabeledLine(experiment, allData, i));
        }

        // Create training & test files
        int numLines = featuredLines.size();
        int numTrainingLines = (int) (numLines*TRAINING_DATA_SET_PERCENT);
        int numValidatingLines = (int) (numLines*VALIDATION_DATA_SET_PERCENT);

        // make all dataset
        makeFile(allLines, allLines.size(), 0, getCsvFileNameAllDataSet(experiment));

        // Make feature files
        makeFile(featuredLines, numTrainingLines, 0, getCsvFileNameTrainigDataSet(experiment, FEATURE.getPrefix()));
        makeFile(featuredLines, numTrainingLines+numValidatingLines, numTrainingLines, getCsvFileNameValidationDataSet(experiment, FEATURE.getPrefix()));
        makeFile(featuredLines, numLines, numTrainingLines+numValidatingLines, getCsvFileNameTestDataSet(experiment, FEATURE.getPrefix()));

        // Make labeled files
        makeFile(labeledLines, numTrainingLines, 0, getCsvFileNameTrainigDataSet(experiment, LABEL.getPrefix()));
        makeFile(labeledLines, numTrainingLines+numValidatingLines, numTrainingLines, getCsvFileNameValidationDataSet(experiment, LABEL.getPrefix()));
        makeFile(labeledLines, numLines, numTrainingLines+numValidatingLines, getCsvFileNameTestDataSet(experiment, LABEL.getPrefix()));
    }

    private String makeLabeledLine(ExperimentDto experiment, List<LoadEntsoe> lines, int i) {
        StringBuilder labeledLine = new StringBuilder();
        for (int j=0; j<experiment.getNumOutputs(); j++) {
            if (labeledLine.toString().equals(""))
                labeledLine.append(lines.get(i+experiment.getNumInputs()+j).getLoadReal());
            else {
                labeledLine.append(",").append(lines.get(i + experiment.getNumInputs() + j).getLoadReal());
            }
        }
        return labeledLine.toString();
    }

    private String makeFeaturedLine(ExperimentDto experiment, List<LoadEntsoe> lines, int i) {
        StringBuilder featuredLine = new StringBuilder();
        for (int j=0; j<experiment.getNumInputs(); j++) {
            if (featuredLine.toString().equals(""))
                featuredLine.append(lines.get(i+j).getLoadReal());
            else
                featuredLine.append(",").append(lines.get(i+j).getLoadReal());
        }
        return featuredLine.toString();
    }

    private void makeFile(List<String> lines, int numLines, int numTrainingLines, String fileName) {
        try (FileWriter csvFile = new FileWriter(fileName)){
            for (int i = numTrainingLines; i < numLines; i++) {
                csvFile.append(lines.get(i));
                csvFile.append("\n");
            }
            csvFile.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

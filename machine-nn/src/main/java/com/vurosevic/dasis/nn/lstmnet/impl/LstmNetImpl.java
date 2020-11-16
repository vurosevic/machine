package com.vurosevic.dasis.nn.lstmnet.impl;

import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.app.service.InputPreparationService;
import com.vurosevic.dasis.nn.lstmnet.LstmNet;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import com.vurosevic.dasis.nn.lstmnet.exception.ExperimentNotSetException;
import lombok.extern.slf4j.Slf4j;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static com.vurosevic.dasis.app.enums.PrefixCsv.FEATURE;
import static com.vurosevic.dasis.app.enums.PrefixCsv.LABEL;

@Slf4j
@Service
public class LstmNetImpl implements LstmNet {

    @Autowired
    private InputPreparationService inputPreparationService;

    @Autowired
    private DataPreparationService dataPreparationService;

    private ExperimentDto experimentDto;

    private static final int HIDDEN_LAYER_WIDTH = 50;
    private static final int HIDDEN_LAYER_CONT = 2;
    private static final int DENSE_LAYER_WIDTH = 100;
    private static final int BATCH_SIZE = 1;

    private static final String MODEL_PATH = "D:\\machine\\models\\";

    private double learningRate;
    private IUpdater updater;
    private DataSet trainingData;
    private DataSet validationData;
    private DataSet testData;
    private DataNormalization normalizer;
    private MultiLayerNetwork net;
    private int epoch;
    private String modelFileName;
    private int batchSize;

    public LstmNetImpl() {
        learningRate = 0.0015; //0.015
        updater = new Adam(learningRate);
        trainingData = null;
        validationData = null;
        testData = null;
        normalizer = null;
        epoch = 1000;
        modelFileName = "lstmNetModel_001.lstm";
        batchSize = BATCH_SIZE;
        experimentDto = null;
    }

    public ExperimentDto getExperimentDto() {
        return experimentDto;
    }

    public void setExperimentDto(ExperimentDto experimentDto) {
        this.experimentDto = experimentDto;
    }

    public Double calculateAvgMape() {
        INDArray testFeaturesAll = getTestFeatures();
        inputPreparationService.setLength(experimentDto.getNumInputs());

        double mapeSum=0.0;

        for (int offsetAll = 0; offsetAll<13195- experimentDto.getNumInputs() - experimentDto.getNumHour(); offsetAll++) {

            // preparing input vector with history data of NUM_INPUTS hours
            for (int i = 0; i < experimentDto.getNumInputs(); i++) {
                inputPreparationService.push(testFeaturesAll.getDouble(Long.valueOf(i) + offsetAll));
            }

            double mapeAll = 0.0;
            INDArray testLabelsAll = getTestLabels();
            double[] predict = predict(inputPreparationService.getInputData());

            for (int i = 0; i < experimentDto.getNumHour(); i++) {
                double label = testLabelsAll.getDouble(Long.valueOf(i) + offsetAll);
                mapeAll += Math.abs(predict[i] - label) / label * 100;
            }
            mapeSum += mapeAll / experimentDto.getNumHour();
            log.info("Average MAPE for test inputs: " + mapeAll / experimentDto.getNumHour());
        }

        return mapeSum/(13195- experimentDto.getNumInputs()-experimentDto.getNumHour());
    }

    public void test(int offset) {
        INDArray testFeatures = getTestFeatures();
        inputPreparationService.setLength(experimentDto.getNumInputs());

        // preparing input vector with history data of NUM_INPUTS hours
        for (int i = 0; i< experimentDto.getNumInputs(); i++) {
            inputPreparationService.push(testFeatures.getDouble(Long.valueOf(i)+offset));
        }

        INDArray testLabels = getTestLabels();
        double mape = 0.0;
        log.info("-------------");
        for (int i = 0; i< experimentDto.getNumInputs(); i++) {
            double predict = predict(inputPreparationService.getInputData())[0];
            double label = testLabels.getDouble(Long.valueOf(i)+offset);
            mape += Math.abs(predict-label)/label*100;
            log.info("Hour " + i + " :" + predict + ",  " + label + ", MAPE=" + Math.abs(predict - label) / label * 100);
            inputPreparationService.push(predict);
        }
        log.info("-------------");
        log.info("Average MAPE for test inputs: " + mape / experimentDto.getNumHour());
    }

    public void trainNetwork(int epoch) {
        setEpoch(epoch);
        trainNetwork();
    }

    @Override
    public void setConfig(ConfigRecord configRecord) {
        learningRate = configRecord.getLearningRate();
        updater = new Adam(learningRate);
        epoch = configRecord.getEpoch();
        modelFileName = configRecord.getModelFileName();
        batchSize = configRecord.getBatchSize();
    }

    public MultiLayerConfiguration getLstmNetworkConfiguration() {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.seed(123);
        builder.biasInit(0);
        builder.miniBatch(true);
        builder.updater(updater);
        builder.weightInit(WeightInit.XAVIER);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        builder.l2(0.0001);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        for (int i = 0; i < HIDDEN_LAYER_CONT; i++) {
            LSTM.Builder hiddenLayerBuilder = new LSTM.Builder();
            hiddenLayerBuilder.nIn(i == 0 ? experimentDto.getNumInputs() : HIDDEN_LAYER_WIDTH);
            hiddenLayerBuilder.nOut(HIDDEN_LAYER_WIDTH);
            hiddenLayerBuilder.activation(Activation.TANH);
            hiddenLayerBuilder.weightInit(WeightInit.XAVIER);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        listBuilder.layer(HIDDEN_LAYER_CONT, new DenseLayer.Builder()
                .nIn(HIDDEN_LAYER_WIDTH)
                .nOut(DENSE_LAYER_WIDTH)
                .activation(Activation.RELU)
                .build());
        listBuilder.layer(HIDDEN_LAYER_CONT+1, new RnnOutputLayer.Builder()
                .nIn(DENSE_LAYER_WIDTH)
                .nOut(experimentDto.getNumOutputs())
                .activation(Activation.IDENTITY)
                .lossFunction(LossFunctions.LossFunction.MSE)
                .build());

        return listBuilder.build();
    }

    public void initNetwork() {
        net = new MultiLayerNetwork(this.getLstmNetworkConfiguration());
        net.init();
    }

    public void trainNetwork() {
        double currentMape=10000;

        for (int ep = 0; ep < this.epoch; ep++) {
            net.fit(trainingData);
            net.rnnClearPreviousState();

            INDArray output = net.rnnTimeStep(testData.getFeatures());
            double mape = getMape(testData, output, normalizer);

            if (mape<currentMape*0.995) {
                this.save(modelFileName);
                currentMape = mape;
            }
            log.info("Epoch: " + ep + ", MIN MAPE: " + currentMape + " MAPE: " + mape);
        }

    }

    public void trainNetworkEarlyStopping() {
        throw new UnsupportedOperationException();
    }

    public double[] predict(double[] inputArray) {
        INDArray input = Nd4j.create(inputArray, new int[]{1, experimentDto.getNumInputs()});
        normalizer.transform(input);
        INDArray out = net.rnnTimeStep(input);
        normalizer.revertLabels(out);
        double[] result = new double[experimentDto.getNumOutputs()];
        for (int i=0; i<experimentDto.getNumOutputs(); i++)
            result[i] = out.getDouble(i);
        return result;
    }

    public double getMape(DataSet testData, INDArray predicted, DataNormalization normalizer) {
        double res = 0.0;
        DataSet copy = testData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        normalizer.revertLabels(predicted);

        for (int i=0; i<lables.length(); i++) {
            res += Math.abs((lables.getDouble(i)-predicted.getDouble(i))/lables.getDouble(i));
        }

        res = res/lables.length()*100;
        return res;
    }

    public boolean loadData() {

        DataSetIterator iteratorTraining;
        DataSetIterator iteratorVerifying;
        DataSetIterator iteratorTest;

        if (experimentDto == null) {
            throw new ExperimentNotSetException("Experiment is not set");
        }

        SequenceRecordReader featureTrainingReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelTrainingReader = new CSVSequenceRecordReader(1, ",");
        try {
            featureTrainingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTrainigDataSet(experimentDto, FEATURE.getPrefix()).replace("_0","_%d"), 0, 0));
            labelTrainingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTrainigDataSet(experimentDto, LABEL.getPrefix()).replace("_0","_%d") , 0, 0));

            iteratorTraining = new SequenceRecordReaderDataSetIterator(featureTrainingReader, labelTrainingReader, batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
            trainingData = iteratorTraining.next();

        } catch (Exception e) {
            return false;
        } finally {
            try {
                featureTrainingReader.close();
                labelTrainingReader.close();
            } catch (IOException|NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        SequenceRecordReader featureValidatingReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelValidatingReader = new CSVSequenceRecordReader(1, ",");
        try {
            featureValidatingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTrainigDataSet(experimentDto, FEATURE.getPrefix()).replace("_0","_%d"), 0, 0));
            labelValidatingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTrainigDataSet(experimentDto, LABEL.getPrefix()).replace("_0","_%d") , 0, 0));

            iteratorVerifying = new SequenceRecordReaderDataSetIterator(featureValidatingReader, labelValidatingReader, batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
            validationData = iteratorVerifying.next();

        } catch (Exception e) {
            return false;
        } finally {
            try {
                featureValidatingReader.close();
                labelValidatingReader.close();
            } catch (IOException|NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        SequenceRecordReader featureTestReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelTestReader = new CSVSequenceRecordReader(1, ",");
        try {
            featureTestReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTestDataSet(experimentDto, FEATURE.getPrefix()).replace("_0","_%d"), 0, 0));
            labelTestReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameTestDataSet(experimentDto, LABEL.getPrefix()).replace("_0","_%d"), 0, 0));
            iteratorTest = new SequenceRecordReaderDataSetIterator(featureTestReader, labelTestReader, batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
            testData = iteratorTest.next();

        } catch (Exception e) {
            return false;
        } finally {
            try {
                featureTestReader.close();
            } catch (IOException|NullPointerException e) {
                log.error(e.getMessage());
            }
            try {
                labelTestReader.close();
            } catch (IOException|NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        normalizer = new NormalizerMinMaxScaler(0,1);
        normalizer.fitLabel(true);
        normalizer.fit(trainingData);
        normalizer.transform(trainingData);
        normalizer.transform(validationData);
        normalizer.transform(testData);
        return true;
    }

    public void loadLastState() {
        log.info("Model FileName: " + modelFileName);
        try {
            load(MODEL_PATH + modelFileName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public void load(String filename) throws IOException {
        try {
            net = ModelSerializer.restoreMultiLayerNetwork(new File(MODEL_PATH + filename));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void save(String filename) {
        try {
            ModelSerializer.writeModel(net, new File(MODEL_PATH + filename), true);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public double getCurrentMape() {
        INDArray output = net.rnnTimeStep(testData.getFeatures());
        return getMape(testData, output, normalizer);
    }

    public DataSet getTrainingData() {
        return trainingData;
    }

    public DataSet getTestData() {
        DataSet copy = testData.copy();
        INDArray lables = copy.getLabels();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        normalizer.revertLabels(lables);
        return testData;
    }

    public INDArray getTrainingFeatures() {
        DataSet copy = trainingData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    public INDArray getTrainingLabels() {
        DataSet copy = trainingData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        return lables;
    }

    public INDArray getTestFeatures() {
        DataSet copy = testData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    public INDArray getTestLabels() {
        DataSet copy = testData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        return lables;
    }

    @Override
    public INDArray getValidationFeatures() {
        DataSet copy = validationData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    @Override
    public INDArray getValidationLabels() {
        DataSet copy = validationData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        return lables;
    }

    public int getEpoch() {
        return epoch;
    }
    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }


}

package com.vurosevic.dasis.nn.lstmnet.impl;

import com.vurosevic.dasis.app.dto.CrossValidationResultDto;
import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.service.CrossValidationService;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.app.service.ExperimentResultService;
import com.vurosevic.dasis.app.service.InputPreparationService;
import com.vurosevic.dasis.nn.lstmnet.LstmSeq2Seq;
import com.vurosevic.dasis.nn.lstmnet.config.ConfigRecord;
import com.vurosevic.dasis.nn.lstmnet.exception.ExperimentNotSetException;
import lombok.extern.slf4j.Slf4j;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
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
public class LstmSeq2SeqImpl implements LstmSeq2Seq {

    @Autowired
    private InputPreparationService inputPreparationService;

    @Autowired
    private DataPreparationService dataPreparationService;

    @Autowired
    private ExperimentResultService experimentResultService;

    @Autowired
    private CrossValidationService crossValidationService;

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
    private ComputationGraph net;
    private int epoch;
    private String modelFileName;
    private int batchSize;

    private INDArray validateFeaturesAll;
    private INDArray validateLabelsAll;

    public LstmSeq2SeqImpl() {
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

    @Override
    public Double calculateAvgMape() {
        INDArray testFeaturesAll = getTestFeatures();
        INDArray testLabelsAll = getTestLabels();
        inputPreparationService.setLength(experimentDto.getNumInputs());

        double mapeSum=0.0;
        Integer count = 0;

        for (int offsetAll = 0; offsetAll<testFeaturesAll.size(2); offsetAll++) {

            // preparing input vector with history data of NUM_INPUTS hours
            for (int i = 0; i < experimentDto.getNumInputs(); i++) {
                inputPreparationService.push(testFeaturesAll.getDouble(1, Long.valueOf(i), offsetAll));
            }

            double mapeAll = 0.0;
            double[] predict = predict(inputPreparationService.getInputData());

            CrossValidationResultDto crossValidationResultDto = crossValidationService.saveCrossValidationResult(experimentDto, count);

            for (int i = 0; i < experimentDto.getNumOutputs(); i++) {
                double label = testLabelsAll.getDouble(1, Long.valueOf(i), offsetAll);
                double mape = Math.abs(predict[i] - label) / label * 100;
                mapeAll += mape;

                crossValidationService.saveCrossValidationDetail(crossValidationResultDto, i, mape, label, predict[i]);
            }
            mapeSum += mapeAll / experimentDto.getNumOutputs();
            count++;
            log.info("Average MAPE for test {} inputs: {}", offsetAll, mapeAll / experimentDto.getNumOutputs());
        }

        return mapeSum/count;
    }

    @Override
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

    @Override
    public ComputationGraphConfiguration getLstmNetworkConfiguration() {

        return new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.25))
                .seed(42)
                .graphBuilder()
                .addInputs("input") //can use any label for this
                .addLayer("L1", new LSTM.Builder().nIn(experimentDto.getNumInputs()).nOut(HIDDEN_LAYER_WIDTH).build(), "input")
                .addLayer("L2", new LSTM.Builder().nIn(HIDDEN_LAYER_WIDTH).nOut(HIDDEN_LAYER_WIDTH).build(), "L1")
                .addLayer("L3", new DenseLayer.Builder().nIn(HIDDEN_LAYER_WIDTH).nOut(HIDDEN_LAYER_WIDTH).build(), "L2")
                .addLayer("L4",new RnnOutputLayer.Builder().nIn(HIDDEN_LAYER_WIDTH).nOut(experimentDto.getNumOutputs()).build(), "L3")
                .setOutputs("L4")
                .build();


//                .weightInit(WeightInit.XAVIER)
//                .updater(new Adam(0.25))
//                .seed(42)
//                .graphBuilder()
//                .addInputs("in_data", "last_in")
//                .setInputTypes(InputType.recurrent(experimentDto.getNumInputs()), InputType.recurrent(experimentDto.getNumInputs()))
//                //The inputs to the encoder will have size = minibatch x featuresize x timesteps
//                //Note that the network only knows of the feature vector size. It does not know how many time steps unless it sees an instance of the data
//                .addLayer("encoder", new LSTM.Builder().nIn(experimentDto.getNumInputs()).nOut(HIDDEN_LAYER_WIDTH).activation(Activation.LEAKYRELU).build(), "in_data")
//                //Create a vertex indicating the very last time step of the encoder layer needs to be directed to other places in the comp graph
//                .addVertex("lastTimeStep", new LastTimeStepVertex("in_data"), "encoder")
//                //Create a vertex that allows the duplication of 2d input to a 3d input
//                //In this case the last time step of the encoder layer (viz. 2d) is duplicated to the length of the timeseries "sumOut" which is an input to the comp graph
//                //Refer to the javadoc for more detail
//                .addVertex("duplicateTimeStep", new DuplicateToTimeSeriesVertex("last_in"), "lastTimeStep")
//                //The inputs to the decoder will have size = size of output of last timestep of encoder (numHiddenNodes) + size of the other input to the comp graph,sumOut (feature vector size)
//                .addLayer("decoder", new LSTM.Builder().nIn(experimentDto.getNumInputs() + HIDDEN_LAYER_WIDTH).nOut(HIDDEN_LAYER_WIDTH).activation(Activation.LEAKYRELU).build(), "last_in","duplicateTimeStep")
//                .addLayer("output", new RnnOutputLayer.Builder().nIn(HIDDEN_LAYER_WIDTH).nOut(experimentDto.getNumOutputs()).activation(Activation.LEAKYRELU).lossFunction(LossFunctions.LossFunction.MSE).build(), "decoder")
//                .setOutputs("output")
//                .build();
    }

    @Override
    public void initNetwork() {
        net = new ComputationGraph(this.getLstmNetworkConfiguration());
        net.init();
    }

    @Override
    public Double calculateTestAvgMape() {

        net.rnnClearPreviousState();
        INDArray[] outputVal = net.rnnTimeStep(testData.getFeatures());

        DataSet copy = testData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        normalizer.revertLabels(outputVal[0]);

        double mapeAll = 0.0;
        for (int j=0; j< outputVal[0].size(2); j++){

            CrossValidationResultDto crossValidationResultDto = crossValidationService.saveCrossValidationResult(experimentDto, j);

            for (int i = 0; i < experimentDto.getNumOutputs(); i++) {
                double out = outputVal[0].getDouble(1, Long.valueOf(i), j);
                double label = lables.getDouble(1, Long.valueOf(i), j);
                double mape = 100*Math.abs(label-out)/label;
                mapeAll += mape;

                crossValidationService.saveCrossValidationDetail(crossValidationResultDto, i, mape, label, out);
                log.info("AVG MAPE: {},{} -> {}",j, i, mape);
            }
        }
        return mapeAll/outputVal[0].size(2)/experimentDto.getNumOutputs();
    }

    @Override
    public void trainNetwork() {
        double currentMape=10000;

        for (int ep = 0; ep < this.epoch; ep++) {
            net.fit(trainingData);
            net.rnnClearPreviousState();

            INDArray[] outputTr = net.rnnTimeStep(trainingData.getFeatures());
            double mapeTr = getMape(trainingData, outputTr[0], normalizer);
            experimentResultService.saveTrainingResult(experimentDto, ep, mapeTr);

            INDArray[] outputVal = net.rnnTimeStep(validationData.getFeatures());
            double mapeVal = getMape(validationData, outputVal[0], normalizer);

            experimentResultService.saveValidationResult(experimentDto, ep, mapeVal);

            if (mapeVal<currentMape*0.997) {
                this.save(modelFileName);
                currentMape = mapeVal;
            }
            log.info("Epoch: " + ep + ", MIN MAPE: " + currentMape + " MAPE: " + mapeVal);
        }

    }

    public void trainNetworkEarlyStopping() {
        throw new UnsupportedOperationException();
    }

    public double[] predict(double[] inputArray) {
        INDArray input = Nd4j.create(inputArray, new int[]{1, experimentDto.getNumInputs()});
        normalizer.transform(input);
        INDArray[] out = net.rnnTimeStep(input);
        normalizer.revertLabels(out[0]);
        double[] result = new double[experimentDto.getNumOutputs()];
        for (int i=0; i<experimentDto.getNumOutputs(); i++)
            result[i] = out[0].getDouble(i);
        return result;
    }

    public double  getMape(DataSet testData, INDArray predicted, DataNormalization normalizer) {
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

            iteratorTraining = new SequenceRecordReaderDataSetIterator(featureTrainingReader, labelTrainingReader, batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
            trainingData = iteratorTraining.next();

        } catch (Exception e) {
            return false;
        } finally {
            try {
                featureTrainingReader.close();
                labelTrainingReader.close();
            } catch (IOException |NullPointerException e) {
                log.error(e.getMessage());
            }
        }

        SequenceRecordReader featureValidatingReader = new CSVSequenceRecordReader(1, ",");
        SequenceRecordReader labelValidatingReader = new CSVSequenceRecordReader(1, ",");
        try {
            featureValidatingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameValidationDataSet(experimentDto, FEATURE.getPrefix()).replace("_0","_%d"), 0, 0));
            labelValidatingReader.initialize(new NumberedFileInputSplit(dataPreparationService.getCsvFileNameValidationDataSet(experimentDto, LABEL.getPrefix()).replace("_0","_%d") , 0, 0));

            iteratorVerifying = new SequenceRecordReaderDataSetIterator(featureValidatingReader, labelValidatingReader, batchSize, 2103, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
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
            iteratorTest = new SequenceRecordReaderDataSetIterator(featureTestReader, labelTestReader, batchSize, -1, true, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_START);
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

        validateFeaturesAll = getValidationFeatures();
        validateLabelsAll = getValidationLabels();
        return true;
    }

    public void loadLastState() {
        log.info("Model FileName: " + modelFileName);
        try {
            load(modelFileName);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public void load(String filename) throws IOException {
        try {
            net = ModelSerializer.restoreComputationGraph(new File(MODEL_PATH + filename));
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
        INDArray[] output = net.rnnTimeStep(testData.getFeatures());
        return getMape(testData, output[0], normalizer);
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

    private INDArray getTrainingFeatures() {
        DataSet copy = trainingData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    private INDArray getTrainingLabels() {
        DataSet copy = trainingData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        return lables;
    }

    private INDArray getTestFeatures() {
        DataSet copy = testData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    private INDArray getTestLabels() {
        DataSet copy = testData.copy();
        INDArray lables = copy.getLabels();
        normalizer.revertLabels(lables);
        return lables;
    }

    private INDArray getValidationFeatures() {
        DataSet copy = validationData.copy();
        INDArray features = copy.getFeatures();
        normalizer.revertFeatures(features);
        return features;
    }

    private INDArray getValidationLabels() {
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

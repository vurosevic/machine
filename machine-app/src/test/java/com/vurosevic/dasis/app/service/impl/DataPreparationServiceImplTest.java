package com.vurosevic.dasis.app.service.impl;

import com.vurosevic.dasis.app.dto.CountryDto;
import com.vurosevic.dasis.app.dto.ExperimentDto;
import com.vurosevic.dasis.app.dto.ModelDto;
import com.vurosevic.dasis.app.dto.ProjectDto;
import com.vurosevic.dasis.app.service.DataPreparationService;
import com.vurosevic.dasis.persistence.model.*;
import com.vurosevic.dasis.persistence.repository.LoadEntsoeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Collections;

import static com.vurosevic.dasis.app.enums.PrefixCsv.FEATURE;
import static com.vurosevic.dasis.app.enums.PrefixCsv.LABEL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
//@DataJpaTest
//@ContextConfiguration(classes = {TestContext.class})
//@EnableJpaRepositories(basePackages="com.vurosevic.dasis.persistence.repository", entityManagerFactoryRef="entityManagerFactory")
//@EntityScan("com.vurosevic.dasis.persistence.model")
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class DataPreparationServiceImplTest {

    private DataPreparationServiceImpl dataPreparationService;
    private ExperimentDto experiment;
    private Experiment experimentEntity;
    private Country countryEntity;

    private final String START_PATH = "D:\\machine\\data\\";

    @Mock
    LoadEntsoeRepository loadEntsoeRepository;

    @Mock
    DataPreparationService dataPreparationServiceMock;

    @Before
    public void setup(){

        ProjectDto project;
        ModelDto model;
        CountryDto country;

        project = ProjectDto.builder()
                .id(1L)
                .name("PROJECT")
                .build();

        model = ModelDto.builder()
                .id(1L)
                .nameModel("LSTM")
                .typeModel("LSTM")
                .configModel("IN-50H-50H-D100-OUT")
                .build();

        country = CountryDto.builder()
                .id(3L)
                .name("Hungary")
                .shortcut("HU")
                .build();

        experiment = ExperimentDto.builder()
                .id(1L)
                .model(model)
                .country(country)
                .project(project)
                .numInputs(50)
                .numOutputs(24)
                .numHour(24)
                .build();

        dataPreparationService = new DataPreparationServiceImpl();
    }

    @Test
    public void getCsvFileNameAllDataSet() {

        final String CSV_FILE_NAME = START_PATH + "PROJECT_HU_LSTM_FULL_DATA_SET_50_24_24.CSV";
        String result = dataPreparationService.getCsvFileNameAllDataSet(experiment);
        assertEquals(CSV_FILE_NAME, result);

    }

    @Test
    public void getCsvFileNameTrainigDataSet() {

        final String CSV_FILE_NAME = START_PATH + "FEATURE_PROJECT_HU_LSTM_TRAINING_DATA_SET_50_24_24.CSV";
        String result = dataPreparationService.getCsvFileNameTrainigDataSet(experiment, FEATURE.getPrefix());
        assertEquals(CSV_FILE_NAME, result);

    }

    @Test
    public void getCsvFileNameValidationDataSet() {

        final String CSV_FILE_NAME = START_PATH + "LABEL_PROJECT_HU_LSTM_VALIDATION_DATA_SET_50_24_24.CSV";
        String result = dataPreparationService.getCsvFileNameValidationDataSet(experiment, LABEL.getPrefix());
        assertEquals(CSV_FILE_NAME, result);

    }

    @Test
    public void getCsvFileNameTestDataSet() {

        final String CSV_FILE_NAME = START_PATH + "LABEL_PROJECT_HU_LSTM_TEST_DATA_SET_50_24_24.CSV";
        String result = dataPreparationService.getCsvFileNameTestDataSet(experiment, LABEL.getPrefix());

        assertEquals(CSV_FILE_NAME, result);

    }

    @Test
    public void makeCsvFiles() {

        countryEntity = Country.builder()
                .id(3L)
                .name("Hungary")
                .shortcut("HU")
                .build();

        when(loadEntsoeRepository.findByCountryIdOrderByLoadDateAscLoadHourAscLoadMinuteAsc(experiment.getCountry().getId()))
                .thenReturn(Collections.singletonList(LoadEntsoe.builder()
                .id(1L)
                .loadDate(LocalDate.now())
                .loadHour(0)
                .loadMinute(0)
                .loadReal(2345)
                .country(countryEntity)
                .build()));

        assertDoesNotThrow(() -> dataPreparationServiceMock.makeCsvFiles(experiment));
    }
}
package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Experiment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExperimentRepositoryTest extends PersistenceTest{

    @Autowired
    ExperimentRepository experimentRepository;

    @Test
    public void findAllTest() {

        List<Experiment> result = experimentRepository.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();

    }

    @Test
    public void experimentStatusTest() {

        Long EXPERIMENT_STATUS_ID = 1L;

        List<Experiment> result = experimentRepository.findByExperimentStatusId(EXPERIMENT_STATUS_ID);

        assertThat(result)
                .isNotNull()
                .isNotEmpty();

    }

}
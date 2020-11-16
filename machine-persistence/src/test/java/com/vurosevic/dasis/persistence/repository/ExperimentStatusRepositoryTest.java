package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.ExperimentStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExperimentStatusRepositoryTest extends PersistenceTest{

    @Autowired
    ExperimentStatusRepository experimentStatusRepository;

    @Test
    public void findAll() {
        List<ExperimentStatus> result = experimentStatusRepository.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

}
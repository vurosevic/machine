package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Experiment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentRepository extends CrudRepository<Experiment, Long> {

    @Override
    List<Experiment> findAll();

    List<Experiment> findByExperimentStatusId(Long id);
}

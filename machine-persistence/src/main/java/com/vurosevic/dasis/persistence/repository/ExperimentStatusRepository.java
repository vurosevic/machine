package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.ExperimentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentStatusRepository extends CrudRepository<ExperimentStatus, Long> {

    @Override
    List<ExperimentStatus> findAll();

}

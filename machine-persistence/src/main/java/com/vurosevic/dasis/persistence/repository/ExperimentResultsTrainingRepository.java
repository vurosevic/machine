package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.ExperimentResultsTraining;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentResultsTrainingRepository extends CrudRepository<ExperimentResultsTraining, Long> {

}

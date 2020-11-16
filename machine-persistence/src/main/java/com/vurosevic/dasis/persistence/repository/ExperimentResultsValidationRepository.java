package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.ExperimentResultsValidation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentResultsValidationRepository extends CrudRepository<ExperimentResultsValidation, Long> {

}

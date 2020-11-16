package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.ExperimentResultsTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentResultsTestRepository extends CrudRepository<ExperimentResultsTest, Long> {

}

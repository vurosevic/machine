package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.CrossValidationResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrossValidationResultRepository extends CrudRepository<CrossValidationResult, Long> {

}

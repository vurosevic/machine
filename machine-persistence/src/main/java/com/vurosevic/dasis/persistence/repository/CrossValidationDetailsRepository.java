package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.CrossValidationDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrossValidationDetailsRepository extends CrudRepository<CrossValidationDetail, Long> {

}

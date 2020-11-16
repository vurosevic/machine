package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends CrudRepository<Model, Long> {

}

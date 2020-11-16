package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

}

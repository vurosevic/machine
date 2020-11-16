package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Country;
import com.vurosevic.dasis.persistence.model.LoadEntsoe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadEntsoeRepository extends CrudRepository<LoadEntsoe, Long> {

    @Override
    List<LoadEntsoe> findAll();

    List<LoadEntsoe> findByCountryOrderByLoadDateAscLoadHourAscLoadMinuteAsc(Country country);

    List<LoadEntsoe> findByCountryIdOrderByLoadDateAscLoadHourAscLoadMinuteAsc(Long countryId);

    List<LoadEntsoe> findByCountryId(Long countryId);
}

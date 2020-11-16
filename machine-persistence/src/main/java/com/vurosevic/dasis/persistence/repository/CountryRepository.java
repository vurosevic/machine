package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {

    @Override
    List<Country> findAll();

    Optional<Country> findById(Long countryId);
}

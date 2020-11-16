package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Country;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryRepositoryTest extends PersistenceTest {

    @Autowired
    CountryRepository countryRepository;

    @Test
    public void countryRepositoryTest() {

        List<Country> result = countryRepository.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();

    }

    @Test
    public void findByCountryId() {

        final Long COUNTRY_ID = 2L;

        Optional<Country> country = countryRepository.findById(COUNTRY_ID);

        assertThat(country)
                .isNotNull()
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", COUNTRY_ID);

    }

}
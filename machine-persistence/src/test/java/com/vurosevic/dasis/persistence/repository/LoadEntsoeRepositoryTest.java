package com.vurosevic.dasis.persistence.repository;

import com.vurosevic.dasis.persistence.model.Country;
import com.vurosevic.dasis.persistence.model.LoadEntsoe;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class LoadEntsoeRepositoryTest extends PersistenceTest{

    @Autowired
    LoadEntsoeRepository loadEntsoeRepository;

    @Test
    public void findAll(){

        List<LoadEntsoe> result = loadEntsoeRepository.findAll();

        assertThat(result)
                .isNotNull()
                .isNotEmpty();

    }

    @Test
    public void findByCountry() {

        final Long COUNTRY_ID = 2L;

        Country country = Country.builder()
                .id(COUNTRY_ID)
                .shortcut("RS")
                .name("Serbia")
                .build();

        List<LoadEntsoe> result = loadEntsoeRepository.findByCountryOrderByLoadDateAscLoadHourAscLoadMinuteAsc(country);

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void findByCountryIdSorted() {

        final Long COUNTRY_ID = 1L;

        List<LoadEntsoe> result = loadEntsoeRepository.findByCountryIdOrderByLoadDateAscLoadHourAscLoadMinuteAsc(COUNTRY_ID);

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    public void findByCountryId() {

        final Long COUNTRY_ID = 1L;

        List<LoadEntsoe> result = loadEntsoeRepository.findByCountryId(COUNTRY_ID);

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
    }

}
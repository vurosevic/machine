package com.vurosevic.dasis.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentDto {

    private Long id;
    private String name;
    private LocalDateTime experimentTime;
    private ModelDto model;
    private ExperimentStatusDto experimentStatus;
    private CountryDto country;
    private Integer numInputs;
    private Integer numOutputs;
    private Integer numHour;
    private ProjectDto project;

}

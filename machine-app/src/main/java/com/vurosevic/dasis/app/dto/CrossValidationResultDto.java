package com.vurosevic.dasis.app.dto;

import com.vurosevic.dasis.persistence.model.Experiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossValidationResultDto {

    private Long id;
    private Experiment experiment;
    private Integer ordinalNumber;

}

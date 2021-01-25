package com.vurosevic.dasis.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossValidationDetailDto {

    private Long id;
    private CrossValidationResultDto crossValidationResultDto;
    private Integer ordinalNumber;
    private Double mape;
    private Double label;
    private Double realValue;

}

package com.vurosevic.dasis.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {

    private Long id;
    private String nameModel;
    private String typeModel;
    private String configModel;

}

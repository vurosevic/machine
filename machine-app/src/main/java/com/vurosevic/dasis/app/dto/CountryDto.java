package com.vurosevic.dasis.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {

    private Long id;
    private String name;
    private String shortcut;
    private String documentType;
    private String processTypeForecast;
    private String processTypeReal;
    private String outBiddingZoneDomain;

}

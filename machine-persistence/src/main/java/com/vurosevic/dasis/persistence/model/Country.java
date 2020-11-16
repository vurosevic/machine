package com.vurosevic.dasis.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="de_country")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @Column(name="Name", nullable=false)
    private String name;

    @Column(name="Shortcut", nullable=false)
    private String shortcut;

    @Column(name="Document_type", nullable=false, unique=true)
    private String documentType;

    @Column(name="Process_type_forecast", nullable=false, unique=true)
    private String processTypeForecast;

    @Column(name="Process_type_real", nullable=false, unique=true)
    private String processTypeReal;

    @Column(name="Outbiddingzone_domain", nullable=false, unique=true)
    private String outBiddingZoneDomain;

}

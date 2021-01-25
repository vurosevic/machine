package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="de_cross_validation_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossValidationDetail {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Cross_validation_fk")
    private CrossValidationResult crossValidationResult;

    @Column(name="Ordinal_number", nullable=false)
    private Integer ordinalNumber;

    @Column(name="Mape", nullable=false)
    private Double mape;

    @Column(name="Label", nullable=false)
    private Double label;

    @Column(name="Real_value", nullable=false)
    private Double realValue;

}

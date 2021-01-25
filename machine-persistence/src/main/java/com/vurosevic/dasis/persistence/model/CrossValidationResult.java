package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="de_cross_validation_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossValidationResult {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Experiment_fk")
    private Experiment experiment;

    @Column(name="Ordinal_number", nullable=false)
    private Integer ordinalNumber;

}

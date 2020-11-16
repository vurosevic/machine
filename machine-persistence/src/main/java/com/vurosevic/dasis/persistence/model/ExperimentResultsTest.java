package com.vurosevic.dasis.persistence.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="de_experiment_results_test")
@Data
public class ExperimentResultsTest {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Experiment_fk")
    private Experiment experiment;

    @Column(name="Epoch", nullable=false)
    private Integer epoch;

    @Column(name="Mape", nullable=false)
    private Double mape;

    @Column(name="Mape_min", nullable=false)
    private Double mapeMin;

}

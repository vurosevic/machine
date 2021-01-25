package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="de_experiment_results_training")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentResultsTraining {

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

}

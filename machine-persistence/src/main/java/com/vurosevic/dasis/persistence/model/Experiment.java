package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="de_experiment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experiment {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @Column(name="Name", nullable=false)
    private String name;

    @Column(name="Experiment_time", nullable=false)
    private LocalDateTime experimentTime;

    @ManyToOne
    @JoinColumn(name = "Model_fk")
    private Model model;

    @ManyToOne
    @JoinColumn(name = "Status_fk")
    private ExperimentStatus experimentStatus;

    @ManyToOne
    @JoinColumn(name = "Country_fk")
    private Country country;

    @Column(name="Num_inputs", nullable=false)
    private Integer numInputs;

    @Column(name="Num_outputs", nullable=false)
    private Integer numOutputs;

    @Column(name="Num_Hours", nullable=false)
    private Integer numHour;

    @ManyToOne
    @JoinColumn(name = "Project_fk")
    private Project project;
}

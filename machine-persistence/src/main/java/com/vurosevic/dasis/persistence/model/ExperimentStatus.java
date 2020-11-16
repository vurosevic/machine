package com.vurosevic.dasis.persistence.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="de_experiment_status")
@Data
public class ExperimentStatus {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @Column(name="Status", nullable=false, unique=true)
    private String status;

}

package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="de_model")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @Column(name="Name", nullable=false, unique=true)
    private String nameModel;

    @Column(name="Type", nullable=false, unique=true)
    private String typeModel;

    @Column(name="Config", nullable=false, unique=true)
    private String configModel;

}

package com.vurosevic.dasis.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="de_load_entsoe")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoadEntsoe {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ID", nullable=false, updatable = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Country_fk")
    private Country country;

    @Column(name = "Load_date")
    private LocalDate loadDate;

    @Column(name = "Load_hour")
    private Integer loadHour;

    @Column(name = "Load_minute")
    private Integer loadMinute;

    @Column(name = "Load_real")
    private Integer loadReal;

}

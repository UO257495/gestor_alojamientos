package com.nayarasanchez.gestor_alojamientos.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "temporadas")
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; 

    @Column(name = "fecha_inicio", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    @JsonIgnore
    private Alojamiento alojamiento;


}

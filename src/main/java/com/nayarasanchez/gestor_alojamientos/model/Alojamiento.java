package com.nayarasanchez.gestor_alojamientos.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "alojamientos")
public class Alojamiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(length = 1000)
    private String foto; 
    
    @Column(name = "tarifa_base", nullable = false)
    private Double tarifaBase;

    @Column(nullable = false)
    private Double latitud;
    
    @Column(nullable = false)
    private Double longitud; 

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonIgnore
    private List<Temporada> temporadas;

}
package com.nayarasanchez.gestor_alojamientos.model;

import java.util.List;

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
import lombok.Data;

@Entity
@Data
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

    @Column(length = 1000)
    private String fotos; // Se puede guardar como URL o JSON de URLs
    
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
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL)
    private List<Temporada> temporadas;

}
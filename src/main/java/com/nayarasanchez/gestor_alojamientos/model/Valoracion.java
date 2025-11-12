package com.nayarasanchez.gestor_alojamientos.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Data
@Table(name = "valoraciones")
public class Valoracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int puntuacion; 

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDate fecha;
    
    @OneToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;


}

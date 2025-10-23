package com.nayarasanchez.gestor_alojamientos.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(unique=true, nullable = false, length = 10)
    private String dni; 

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String telefono;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Alojamiento> alojamientos;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reserva> reservas;

    // -------------------------
    // Métodos de UserDetails
    // -------------------------
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convención de Spring Security: "ROLE_" + nombre del rol
        return Collections.singleton(() -> "ROLE_" + rol.name());
    }

    @Override
    public String getUsername() {
        return email;
    }


}


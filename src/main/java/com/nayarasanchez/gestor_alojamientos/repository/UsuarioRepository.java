package com.nayarasanchez.gestor_alojamientos.repository;

import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
    @Query("select new java.lang.Boolean(count(*) > 0) from Usuario u where u.email = :email")
    public Boolean comprobarEmailEnUso(String email);

    @Query("select u from Usuario u where u.email = :email")
    public Optional<Usuario> buscarUsuarioPorEmail(String email);

    public Optional<Usuario> findByEmail(String email);

    public List<Usuario> findAllByRol(Rol cliente);
}

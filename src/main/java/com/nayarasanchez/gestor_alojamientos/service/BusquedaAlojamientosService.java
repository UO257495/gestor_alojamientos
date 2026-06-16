package com.nayarasanchez.gestor_alojamientos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusquedaAlojamientosService {

    private final AlojamientoRepository alojamientoRepository;
    
    @PersistenceContext
    private EntityManager em;

    public List<Alojamiento> listarTodos() {
        return alojamientoRepository.findAll();
    }

    public List<Alojamiento> buscarPorFiltro(BusquedaAlojamientoForm form) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Alojamiento> cq = cb.createQuery(Alojamiento.class);
        Root<Alojamiento> alojamiento = cq.from(Alojamiento.class);

        List<Predicate> predicates = new ArrayList<>();

        if (form.getUbicacion() != null && !form.getUbicacion().isBlank()) {
            predicates.add(cb.like(cb.lower(alojamiento.get("direccion")),
                                   "%" + form.getUbicacion().toLowerCase() + "%"));
        }

        if (form.getPersonas() != null && form.getPersonas() > 0) {
            predicates.add(cb.ge(alojamiento.get("capacidad"), form.getPersonas()));
        }

        if (form.getFechaInicio() != null && form.getFechaFin() != null) {
            Subquery<Reserva> subquery = cq.subquery(Reserva.class);
            Root<Reserva> reserva = subquery.from(Reserva.class);
            subquery.select(reserva);

            Predicate mismoAlojamiento = cb.equal(reserva.get("alojamiento"), alojamiento);
            Predicate estadoBloquea = reserva.get("estado").in("CONFIRMADA", "PENDIENTE");
            Predicate fechaSolapada = cb.and(
                    cb.lessThan(reserva.get("fechaInicio"), form.getFechaFin()), 
                    cb.greaterThan(reserva.get("fechaFin"), form.getFechaInicio()) 
            );

            subquery.where(cb.and(mismoAlojamiento, estadoBloquea, fechaSolapada));

            predicates.add(cb.not(cb.exists(subquery)));
        }

        cq.select(alojamiento)
          .where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
    

}

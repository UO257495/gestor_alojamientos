package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoPago;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.FormaPago;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ReservaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;
import com.nayarasanchez.gestor_alojamientos.service.EmailService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;

@SpringBootTest
@Transactional
class ReservaIntegrationTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @MockBean
    private EmailService emailService;

    @Test
    void PI03_crearReservaAsociadaAClienteYAlojamiento() {
        Usuario cliente = crearCliente();
        Alojamiento alojamiento = crearAlojamiento();

        ReservaForm form = new ReservaForm();
        form.setClienteId(cliente.getId());
        form.setAlojamientoId(alojamiento.getId());
        form.setFechaInicio(LocalDate.of(2026, 7, 1));
        form.setFechaFin(LocalDate.of(2026, 7, 5));
        form.setFormaPago(FormaPago.TRANSFERENCIA);

        Reserva guardada = reservaService.crearReservaCliente(form);

        Reserva encontrada = reservaRepository.findById(guardada.getId()).orElseThrow();

        assertNotNull(encontrada.getId());
        assertEquals(cliente.getId(), encontrada.getCliente().getId());
        assertEquals(alojamiento.getId(), encontrada.getAlojamiento().getId());
        assertEquals(EstadoReserva.PENDIENTE, encontrada.getEstado());
        assertEquals(EstadoPago.PENDIENTE, encontrada.getEstadoPago());
        assertEquals(320.00, encontrada.getPrecioTotal());
    }

    @Test
    void PI04_impedirReservaSolapada() {
        Usuario cliente = crearCliente();
        Alojamiento alojamiento = crearAlojamiento();

        ReservaForm reservaExistente = new ReservaForm();
        reservaExistente.setClienteId(cliente.getId());
        reservaExistente.setAlojamientoId(alojamiento.getId());
        reservaExistente.setFechaInicio(LocalDate.of(2026, 7, 10));
        reservaExistente.setFechaFin(LocalDate.of(2026, 7, 15));
        reservaExistente.setFormaPago(FormaPago.TRANSFERENCIA);

        reservaService.crearReservaCliente(reservaExistente);

        ReservaForm reservaSolapada = new ReservaForm();
        reservaSolapada.setClienteId(cliente.getId());
        reservaSolapada.setAlojamientoId(alojamiento.getId());
        reservaSolapada.setFechaInicio(LocalDate.of(2026, 7, 12));
        reservaSolapada.setFechaFin(LocalDate.of(2026, 7, 18));
        reservaSolapada.setFormaPago(FormaPago.TRANSFERENCIA);

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReservaCliente(reservaSolapada));
    }

    private Usuario crearCliente() {
        String identificador = String.valueOf(System.nanoTime());
        String dni = identificador.substring(identificador.length() - 8) + "A";

        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Reserva");
        cliente.setDni(dni);
        cliente.setTelefono("600000000");
        cliente.setEmail("cliente" + identificador + "@test.com");
        cliente.setPassword("passwordCifrada");
        cliente.setRol(Rol.CLIENTE);

        return usuarioRepository.save(cliente);
    }

    private Alojamiento crearAlojamiento() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural reserva");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento para prueba de reserva.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);
        return alojamientoRepository.save(alojamiento);
    }
}
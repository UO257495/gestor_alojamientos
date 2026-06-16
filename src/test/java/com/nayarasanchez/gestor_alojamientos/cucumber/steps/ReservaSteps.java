package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReservaSteps {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @MockitoBean
    private EmailService emailService;

    private Usuario cliente;
    private Alojamiento alojamiento;
    private Reserva reservaCreada;
    private Exception excepcion;

    @Given("existe un cliente y un alojamiento disponible")
    public void existeClienteYAlojamientoDisponible() {
        cliente = crearCliente();
        alojamiento = crearAlojamiento();
    }

    @When("el cliente realiza una reserva válida")
    public void clienteRealizaReservaValida() {
        ReservaForm form = new ReservaForm();
        form.setClienteId(cliente.getId());
        form.setAlojamientoId(alojamiento.getId());
        form.setFechaInicio(LocalDate.of(2026, 7, 1));
        form.setFechaFin(LocalDate.of(2026, 7, 5));
        form.setFormaPago(FormaPago.TRANSFERENCIA);

        reservaCreada = reservaService.crearReservaCliente(form);
    }

    @Then("la reserva se registra con estado PENDIENTE")
    public void reservaRegistradaPendiente() {
        Reserva encontrada = reservaRepository.findById(reservaCreada.getId()).orElseThrow();

        assertEquals(EstadoReserva.PENDIENTE, encontrada.getEstado());
        assertEquals(cliente.getId(), encontrada.getCliente().getId());
        assertEquals(alojamiento.getId(), encontrada.getAlojamiento().getId());
    }

    @Given("existe una reserva previa para el alojamiento")
    public void existeReservaPreviaParaAlojamiento() {
        cliente = crearCliente();
        alojamiento = crearAlojamiento();

        ReservaForm form = new ReservaForm();
        form.setClienteId(cliente.getId());
        form.setAlojamientoId(alojamiento.getId());
        form.setFechaInicio(LocalDate.of(2026, 7, 10));
        form.setFechaFin(LocalDate.of(2026, 7, 15));
        form.setFormaPago(FormaPago.TRANSFERENCIA);

        reservaService.crearReservaCliente(form);
    }

    @When("el cliente intenta realizar una reserva solapada")
    public void clienteIntentaReservaSolapada() {
        try {
            ReservaForm form = new ReservaForm();
            form.setClienteId(cliente.getId());
            form.setAlojamientoId(alojamiento.getId());
            form.setFechaInicio(LocalDate.of(2026, 7, 12));
            form.setFechaFin(LocalDate.of(2026, 7, 18));
            form.setFormaPago(FormaPago.TRANSFERENCIA);

            reservaService.crearReservaCliente(form);
        } catch (Exception e) {
            excepcion = e;
        }
    }

    @Then("el sistema impide crear la reserva")
    public void sistemaImpideCrearReserva() {
        assertNotNull(excepcion);
        assertTrue(excepcion instanceof IllegalArgumentException);
    }

    private Usuario crearCliente() {
        String identificador = String.valueOf(System.nanoTime());
        String dni = identificador.substring(identificador.length() - 8) + "A";

        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Cucumber");
        cliente.setDni(dni);
        cliente.setTelefono("600000000");
        cliente.setEmail("cliente" + identificador + "@test.com");
        cliente.setPassword("passwordCifrada");
        cliente.setRol(Rol.CLIENTE);

        return usuarioRepository.save(cliente);
    }

    private Alojamiento crearAlojamiento() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural Cucumber");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento para prueba de sistema.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);

        return alojamientoRepository.save(alojamiento);
    }
}
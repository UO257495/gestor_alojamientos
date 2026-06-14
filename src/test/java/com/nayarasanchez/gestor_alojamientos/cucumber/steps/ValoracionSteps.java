package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.model.Valoracion;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ReservaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ValoracionRepository;
import com.nayarasanchez.gestor_alojamientos.service.ValoracionService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;

public class ValoracionSteps {

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Usuario cliente;
    private Alojamiento alojamiento;
    private Reserva reserva;

    @Given("existe una reserva finalizada")
    public void existeReservaFinalizada() {
        cliente = crearCliente();
        alojamiento = crearAlojamiento();

        reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setAlojamiento(alojamiento);
        reserva.setFechaInicio(LocalDate.now().minusDays(5));
        reserva.setFechaFin(LocalDate.now().minusDays(2));
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setPrecioTotal(240.00);

        reserva = reservaRepository.save(reserva);
    }

    @When("el cliente registra una valoración")
    public void clienteRegistraValoracion() {
        valoracionService.crearValoracion(reserva, cliente, 5, "Estancia muy satisfactoria.");
    }

    @Then("la valoración queda asociada a la reserva y al alojamiento")
    public void valoracionAsociadaReservaYAlojamiento() {
        Valoracion valoracion = valoracionRepository.findByReserva(reserva).orElseThrow();

        assertEquals(reserva.getId(), valoracion.getReserva().getId());
        assertEquals(cliente.getId(), valoracion.getCliente().getId());
        assertEquals(alojamiento.getId(), valoracion.getReserva().getAlojamiento().getId());
        assertEquals(5, valoracion.getPuntuacion());
    }

    private Usuario crearCliente() {
        String identificador = String.valueOf(System.nanoTime());

        Usuario cliente = new Usuario();
        cliente.setNombre("Cliente Valoracion");
        cliente.setDni(identificador.substring(identificador.length() - 8) + "V");
        cliente.setTelefono("600000002");
        cliente.setEmail("valoracion" + identificador + "@test.com");
        cliente.setPassword("passwordCifrada");
        cliente.setRol(Rol.CLIENTE);

        return usuarioRepository.save(cliente);
    }

    private Alojamiento crearAlojamiento() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural valoración");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento para prueba de valoración.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);

        return alojamientoRepository.save(alojamiento);
    }
}
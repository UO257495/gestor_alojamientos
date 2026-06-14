Feature: Gestión de reservas

  Scenario: PS-03 Creación de una reserva válida por parte del cliente
    Given existe un cliente y un alojamiento disponible
    When el cliente realiza una reserva válida
    Then la reserva se registra con estado PENDIENTE

  Scenario: PS-04 Intento de reserva con fechas no disponibles
    Given existe una reserva previa para el alojamiento
    When el cliente intenta realizar una reserva solapada
    Then el sistema impide crear la reserva
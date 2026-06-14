Feature: Valoración de estancia

  Scenario: PS-07 Registro de una valoración para una estancia finalizada
    Given existe una reserva finalizada
    When el cliente registra una valoración
    Then la valoración queda asociada a la reserva y al alojamiento
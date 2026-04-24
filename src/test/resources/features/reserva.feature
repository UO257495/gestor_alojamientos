Feature: Gestión de reservas

  Scenario: Crear una reserva con fechas válidas
    Given existe un alojamiento con precio base de 80 euros
    When el cliente realiza una reserva del 1 de julio de 2026 al 5 de julio de 2026
    Then el sistema calcula un total de 320 euros

  Scenario: Rechazar una reserva con fechas inválidas
    Given existe un alojamiento con precio base de 80 euros
    When el cliente realiza una reserva del 5 de julio de 2026 al 1 de julio de 2026
    Then el sistema rechaza la reserva

  Scenario: No cobrar el día de salida
    Given existe un alojamiento con precio base de 100 euros
    When el cliente realiza una reserva del 10 de julio de 2026 al 12 de julio de 2026
    Then el sistema calcula un total de 200 euros
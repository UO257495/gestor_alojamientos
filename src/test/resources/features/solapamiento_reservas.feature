# language: es
Característica: Validación de solapamientos de reservas
  Como sistema de reservas
  Quiero impedir reservas que coincidan con noches ya ocupadas
  Para evitar conflictos entre clientes

  Antecedentes:
    Dado que existe un alojamiento "Oviedo" con una tarifa base de 70.0 euros
    Y existe un cliente "Cliente Prueba" con email "cliente@test.com" y dni "12345678A"

  Escenario: No permitir una reserva dentro de otra ya confirmada
    Y existe una reserva confirmada para ese alojamiento desde el "25/11/2026" hasta el "28/11/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "26/11/2026" hasta el "27/11/2026"
    Entonces el sistema debe indicar que las fechas se solapan

  Escenario: No permitir una reserva con solapamiento parcial al inicio
    Y existe una reserva confirmada para ese alojamiento desde el "25/11/2026" hasta el "28/11/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "24/11/2026" hasta el "26/11/2026"
    Entonces el sistema debe indicar que las fechas se solapan

  Escenario: No permitir una reserva con solapamiento parcial al final
    Y existe una reserva confirmada para ese alojamiento desde el "25/11/2026" hasta el "28/11/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "27/11/2026" hasta el "29/11/2026"
    Entonces el sistema debe indicar que las fechas se solapan

  Escenario: Permitir reserva cuando el check-in coincide con el checkout previo
    Y existe una reserva confirmada para ese alojamiento desde el "25/11/2026" hasta el "28/11/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "28/11/2026" hasta el "30/11/2026"
    Entonces la reserva debe ser válida

  Escenario: Una reserva cancelada no bloquea fechas
    Y existe una reserva cancelada para ese alojamiento desde el "10/12/2026" hasta el "12/12/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "10/12/2026" hasta el "12/12/2026"
    Entonces la reserva debe ser válida

  Escenario: Una reserva rechazada no bloquea fechas
    Y existe una reserva rechazada para ese alojamiento desde el "15/12/2026" hasta el "17/12/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "15/12/2026" hasta el "17/12/2026"
    Entonces la reserva debe ser válida

  Escenario: Una reserva pendiente sí bloquea fechas
    Y existe una reserva pendiente para ese alojamiento desde el "20/12/2026" hasta el "22/12/2026"
    Cuando intento crear una reserva de cliente para ese alojamiento desde el "21/12/2026" hasta el "23/12/2026"
    Entonces el sistema debe indicar que las fechas se solapan
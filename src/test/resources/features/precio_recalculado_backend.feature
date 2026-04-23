# language: es
Característica: Recalculo seguro del precio de la reserva en backend
  Como sistema
  Quiero recalcular el precio total al guardar la reserva
  Para no depender del precio enviado por el cliente

  Antecedentes:
    Dado que existe un alojamiento "Piloña" con una tarifa base de 40.0 euros
    Y existe un cliente "Enrique" con email "enrique@test.com" y dni "33333333C"

  Escenario: El sistema ignora un precio manipulado enviado por el cliente
    Cuando un cliente crea una reserva para ese alojamiento desde el "25/02/2026" hasta el "27/02/2026" con un precio enviado de 1.0 euros
    Entonces la reserva debe ser válida
    Y la reserva se guarda con estado "PENDIENTE"
    Y la reserva se guarda con precio total de 80.0 euros
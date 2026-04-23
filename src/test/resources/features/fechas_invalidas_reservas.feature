# language: es
Característica: Validación de fechas inválidas al crear reservas
  Como sistema
  Quiero impedir reservas con rangos de fechas inválidos
  Para garantizar la consistencia de los datos

  Antecedentes:
    Dado que existe un alojamiento "La Isla" con una tarifa base de 60.0 euros
    Y existe un cliente "Patricia" con email "patricia@test.com" y dni "44444444D"

  Escenario: No permitir una reserva con fecha fin anterior a fecha inicio
    Cuando intento crear una reserva de cliente inválida para ese alojamiento desde el "15/05/2026" hasta el "12/05/2026"
    Entonces el sistema debe indicar que la fecha de fin debe ser posterior a la de inicio

  Escenario: No permitir una reserva con misma fecha de inicio y fin
    Cuando intento crear una reserva de cliente inválida para ese alojamiento desde el "15/05/2026" hasta el "15/05/2026"
    Entonces el sistema debe indicar que la fecha de fin debe ser posterior a la de inicio
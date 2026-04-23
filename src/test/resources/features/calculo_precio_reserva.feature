# language: es
Característica: Cálculo del precio de reservas por noches y temporadas
  Como cliente de la plataforma
  Quiero que el precio de la reserva se calcule noche a noche
  Para pagar solo las noches disfrutadas y los suplementos de temporada que correspondan

  Antecedentes:
    Dado que existe un alojamiento "San Miguel" con una tarifa base de 70.0 euros
    Y existe una temporada "Verano" para ese alojamiento desde el "01/06/2026" hasta el "31/08/2026" con un suplemento de 30.0 euros

  Escenario: Reserva completa en temporada baja
    Cuando calculo el total de la reserva desde el "10/05/2026" hasta el "13/05/2026"
    Entonces el sistema debe calcular 3 noches
    Y el precio total debe ser 210.0 euros

  Escenario: Reserva completa en temporada alta
    Cuando calculo el total de la reserva desde el "10/07/2026" hasta el "13/07/2026"
    Entonces el sistema debe calcular 3 noches
    Y el precio total debe ser 300.0 euros

  Escenario: Reserva que termina el día final de la temporada
    Cuando calculo el total de la reserva desde el "30/08/2026" hasta el "01/09/2026"
    Entonces el sistema debe calcular 2 noches
    Y el precio total debe ser 200.0 euros

  Escenario: El checkout no se cobra aunque coincida fuera de temporada
    Cuando calculo el total de la reserva desde el "31/08/2026" hasta el "01/09/2026"
    Entonces el sistema debe calcular 1 noches
    Y el precio total debe ser 100.0 euros

  Escenario: Fecha fin anterior a fecha inicio
    Cuando calculo el total de la reserva desde el "15/05/2026" hasta el "12/05/2026"
    Entonces el precio total debe ser 0.0 euros
# language: es
Característica: Cálculo del precio de reservas por noches y temporadas
  Como cliente de la plataforma
  Quiero que el precio de mi reserva se calcule sumando el precio de cada noche exacta
  Para pagar lo justo, incluso si mi estancia abarca días de diferentes temporadas

  Antecedentes: Configuración base del alojamiento
    Dado que existe un alojamiento "Casa Rural" con una tarifa base de 50.0 euros
    Y tiene una temporada alta "Verano" desde el "01/08/2026" al "31/08/2026" con un suplemento de 20.0 euros

  Escenario: Reserva sencilla en temporada baja (Cálculo puro de noches)
    Cuando realizo una reserva desde el "10/05/2026" hasta el "13/05/2026"
    Entonces el sistema debe calcular una estancia de 3 noches
    Y el precio total debe ser 150.0 euros
    # Explicación para el TFG: 3 noches en temporada baja (50€ x 3)

  Escenario: Reserva que empieza en temporada alta y termina en temporada baja (Transición)
    Cuando realizo una reserva desde el "30/08/2026" hasta el "02/09/2026"
    Entonces el precio total debe ser 190.0 euros
    # Explicación matemática: 
    # - Noche del 30 de Agosto (Temporada alta): 50 + 20 = 70€
    # - Noche del 31 de Agosto (Temporada alta): 50 + 20 = 70€
    # - Noche del 01 de Septiembre (Temporada baja): 50€
    # - Día 02 de Septiembre (Checkout): No se cobra. 
    # Total: 70 + 70 + 50 = 190€

  Escenario: El día de salida (Checkout) coincide con el inicio de la temporada alta
    Cuando realizo una reserva desde el "29/07/2026" hasta el "01/08/2026"
    Entonces el precio total debe ser 150.0 euros
    # Explicación: Las noches en las que duerme (29, 30 y 31) son baja. 
    # Se va el día 1 por la mañana, así que NO se le aplica la tarifa alta del día 1.
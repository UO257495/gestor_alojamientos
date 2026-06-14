Feature: Gestión de temporadas

  Scenario: PS-05 Creación de una temporada válida por parte del propietario
    Given existe un alojamiento para gestionar temporadas
    When el propietario crea una temporada válida
    Then la temporada queda registrada correctamente

  Scenario: PS-06 Intento de crear una temporada solapada
    Given existe una temporada previa para el alojamiento
    When el propietario intenta crear una temporada solapada
    Then el sistema muestra un error y no guarda la temporada
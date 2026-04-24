Feature: Gestión de temporadas

  Scenario: Crear una temporada válida
    Given existe una temporada del 1 de julio de 2026 al 15 de julio de 2026
    When se crea otra temporada del 16 de julio de 2026 al 31 de julio de 2026
    Then el sistema permite crear la temporada

  Scenario: Rechazar temporadas solapadas
    Given existe una temporada del 1 de julio de 2026 al 15 de julio de 2026
    When se crea otra temporada del 10 de julio de 2026 al 20 de julio de 2026
    Then el sistema rechaza la temporada
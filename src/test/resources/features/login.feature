Feature: Inicio de sesión

  Scenario: PS-01 Inicio de sesión con credenciales válidas
    Given existe un usuario registrado con credenciales válidas
    When el usuario inicia sesión
    Then accede al panel correspondiente a su rol
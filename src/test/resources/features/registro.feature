Feature: Registro de usuario

  Scenario: PS-02 Registro de un nuevo cliente con datos válidos
    Given el usuario no está registrado en la plataforma
    When completa el formulario de registro con datos válidos
    Then la cuenta se crea correctamente con rol CLIENTE
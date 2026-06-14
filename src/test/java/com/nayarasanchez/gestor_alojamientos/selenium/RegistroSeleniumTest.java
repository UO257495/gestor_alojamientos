package com.nayarasanchez.gestor_alojamientos.selenium;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class RegistroSeleniumTest {

    @Test
    void PUI02_comprobarCargaFormularioRegistro() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://localhost:8080/gestion/usuarios/nuevo");

            WebElement nombre = driver.findElement(By.id("nombre"));
            WebElement dni = driver.findElement(By.id("dni"));
            WebElement telefono = driver.findElement(By.id("telefono"));
            WebElement email = driver.findElement(By.id("email"));
            WebElement password = driver.findElement(By.id("password"));
            WebElement confirmarPassword = driver.findElement(By.id("confirmarPassword"));

            assertTrue(nombre.isDisplayed());
            assertTrue(dni.isDisplayed());
            assertTrue(telefono.isDisplayed());
            assertTrue(email.isDisplayed());
            assertTrue(password.isDisplayed());
            assertTrue(confirmarPassword.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
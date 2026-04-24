package com.nayarasanchez.gestor_alojamientos.selenium;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class ReservaSeleniumTest {

    @Test
    void formularioReservaDebeMostrarCamposPrincipales() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://localhost:8080/login");

            driver.findElement(By.id("email")).sendKeys("nayarasanchz@gmail.com");
            driver.findElement(By.id("password")).sendKeys("TfgNayara98!");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.get("http://localhost:8080/gestion/reservas/detalle");

            WebElement fechaInicio = driver.findElement(By.id("fechaInicioInput"));
            WebElement fechaFin = driver.findElement(By.id("fechaFinInput"));
            WebElement alojamiento = driver.findElement(By.id("alojamientoSelect"));
            WebElement cliente = driver.findElement(By.id("clienteSelect"));

            assertTrue(fechaInicio.isDisplayed());
            assertTrue(fechaFin.isDisplayed());
            assertTrue(alojamiento.isDisplayed());
            assertTrue(cliente.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
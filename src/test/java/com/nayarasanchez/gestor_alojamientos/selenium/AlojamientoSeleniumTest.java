package com.nayarasanchez.gestor_alojamientos.selenium;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class AlojamientoSeleniumTest {

    @Test
    void formularioAlojamientoDebeMostrarCamposPrincipales() {
        WebDriver driver = new ChromeDriver();

        try {
            
            driver.get("http://localhost:8080/login");

            driver.findElement(By.id("email")).sendKeys("nayarasanchz@gmail.com");
            driver.findElement(By.id("password")).sendKeys("TfgNayara98!");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.get("http://localhost:8080/gestion/alojamientos/detalle");

            WebElement nombre = driver.findElement(By.id("nombre"));
            WebElement direccion = driver.findElement(By.id("direccion"));
            WebElement descripcion = driver.findElement(By.id("descripcion"));
            WebElement capacidad = driver.findElement(By.id("capacidadInput"));

            assertNotNull(nombre);
            assertNotNull(direccion);
            assertNotNull(descripcion);
            assertNotNull(capacidad);

        } finally {
            driver.quit();
        }
    }
}
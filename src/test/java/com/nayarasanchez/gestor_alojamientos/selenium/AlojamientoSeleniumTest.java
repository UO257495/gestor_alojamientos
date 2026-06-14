package com.nayarasanchez.gestor_alojamientos.selenium;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class AlojamientoSeleniumTest {

    @Test
    void PUI03_comprobarCargaFormularioAlojamiento() {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {

            driver.get("http://localhost:8080/login");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                    .sendKeys("TEST_EMAIL");

            driver.findElement(By.id("password")).sendKeys("TEST_PASS");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.get("http://localhost:8080/gestion/alojamientos/detalle");

            WebElement nombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));

            WebElement direccion = driver.findElement(By.id("direccion"));
            WebElement descripcion = driver.findElement(By.id("descripcion"));
            WebElement capacidad = driver.findElement(By.id("capacidadInput"));
            WebElement tarifa = driver.findElement(By.id("tarifaBaseInput"));
            WebElement foto = driver.findElement(By.id("foto"));

            assertTrue(nombre.isDisplayed());
            assertTrue(direccion.isDisplayed());
            assertTrue(descripcion.isDisplayed());
            assertTrue(capacidad.isDisplayed());
            assertTrue(tarifa.isDisplayed());
            assertTrue(foto.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
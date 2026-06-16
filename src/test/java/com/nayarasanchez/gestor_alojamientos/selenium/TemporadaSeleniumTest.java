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

import io.github.cdimascio.dotenv.Dotenv;

class TemporadaSeleniumTest {

    @Test
    void PUI05_comprobarCargaFormularioTemporada() {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Dotenv dotenv = Dotenv.load();

        String email = dotenv.get("TEST_EMAIL");
        String password = dotenv.get("TEST_PASS");

        try {
            driver.get("http://localhost:8080/login");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                    .sendKeys(email);

            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.not(
                    ExpectedConditions.urlContains("/login")
            ));

            driver.get("http://localhost:8080/gestion/temporadas/detalle");

            System.out.println("URL actual: " + driver.getCurrentUrl());

            assertFalse(
                    driver.getCurrentUrl().contains("/login"),
                    "Selenium ha sido redirigido al login. Revisa usuario, contraseña o permisos."
            );

            WebElement nombre = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("nombre"))
            );

            WebElement fechaInicio = driver.findElement(By.id("fechaInicioInput"));
            WebElement fechaFin = driver.findElement(By.id("fechaFinInput"));
            WebElement precio = driver.findElement(By.name("precio"));

            assertTrue(nombre.isDisplayed());
            assertTrue(fechaInicio.isDisplayed());
            assertTrue(fechaFin.isDisplayed());
            assertTrue(precio.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
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

class ReservaSeleniumTest {

    @Test
    void PUI04_comprobarCargaFormularioReserva() {

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Dotenv dotenv = Dotenv.load();

        String email = dotenv.get("TEST_EMAIL");
        String password = dotenv.get("TEST_PASS");

        try {

            driver.get("http://localhost:8080/login");

            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("password")).sendKeys(password);
            
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.get("http://localhost:8080/gestion/reservas/detalle");

            WebElement fechaInicio =
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fechaInicioInput")));

            WebElement fechaFin = driver.findElement(By.id("fechaFinInput"));
            WebElement formaPago = driver.findElement(By.id("formaPagoSelect"));
            WebElement botonConfirmar =
                    driver.findElement(By.cssSelector("button[type='submit']"));

            assertTrue(fechaInicio.isDisplayed());
            assertTrue(fechaFin.isDisplayed());
            assertTrue(formaPago.isDisplayed());
            assertTrue(botonConfirmar.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
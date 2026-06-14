package com.nayarasanchez.gestor_alojamientos.selenium;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

class LoginSeleniumTest {

    @Test
    void PUI01_comprobarCargaFormularioLogin() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://localhost:8080/login");

            WebElement email = driver.findElement(By.name("email"));
            WebElement password = driver.findElement(By.name("password"));
            WebElement botonAcceso = driver.findElement(By.cssSelector("button[type='submit']"));

            assertTrue(email.isDisplayed());
            assertTrue(password.isDisplayed());
            assertTrue(botonAcceso.isDisplayed());

        } finally {
            driver.quit();
        }
    }
}
package com.nayarasanchez.gestor_alojamientos.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        Dotenv dotenv = Dotenv.load(); // Carga .env automáticamente desde la raíz del proyecto

        return DataSourceBuilder.create()
                .url(dotenv.get("DB_URL"))         // jdbc:postgresql://host:port/db
                .username(dotenv.get("DB_USER"))   // Usuario maestro de RDS
                .password(dotenv.get("DB_PASSWORD")) // Contraseña
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

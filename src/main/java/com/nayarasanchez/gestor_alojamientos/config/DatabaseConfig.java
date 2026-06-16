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
        Dotenv dotenv = Dotenv.load(); 

        return DataSourceBuilder.create()
                .url(dotenv.get("DB_URL"))      
                .username(dotenv.get("DB_USER"))   
                .password(dotenv.get("DB_PASSWORD")) 
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

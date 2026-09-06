package com.pokedex;

import com.pokedex.config.PokedexProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Bootstraps the Pokedex Spring Boot application.
 */
@SpringBootApplication
@EnableConfigurationProperties(PokedexProperties.class)
public class PokedexApplication {

    /**
     * Starts the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PokedexApplication.class, args);
    }
}
package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class DockerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DockerApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(DockerApplication.class);

	/**
	 * Wypisuje informacje o aplikacji przy jej uruchomieniu: czas włączenia, port TCP oraz autor.
	 * @param webServerContext informacje o serwerze
	 */
	@Bean
	public CommandLineRunner runner(ServletWebServerApplicationContext webServerContext) {
		return (args) -> {
				log.info("Aplikacja uruchomiona o czasie: {}. Nasłuchiwanie na porcie {}.", LocalDateTime.now(), webServerContext.getWebServer().getPort());
				log.info("Autor aplikacji: Bartłomiej Sulima");
		};
	}

}

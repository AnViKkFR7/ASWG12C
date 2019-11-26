package com.aswg12c.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class AswApplication {

	public static void main(String[] args) {
		SpringApplication.run(AswApplication.class, args);
	}
}

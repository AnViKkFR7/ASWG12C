package com.aswg12c.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
@EnableOAuth2Sso
public class AswApplication {

	public static void main(String[] args) {
		SpringApplication.run(AswApplication.class, args);
	}
}

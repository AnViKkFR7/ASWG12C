package com.aswg12c.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.security.Principal;
import java.util.LinkedHashMap;

@EnableJpaRepositories
@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class AswApplication extends WebSecurityConfigurerAdapter {

	public String tokenValue;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/**")
				.authorizeRequests()
				.antMatchers("/", "/login**", "/webjars/**", "/error**")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and().logout().logoutSuccessUrl("/").permitAll()
				.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

	}

	@RequestMapping("/user")
	public Authentication user(Authentication authentication) {
		Object t = authentication.getDetails();
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)t;
		tokenValue = details.getTokenValue();
		return authentication;
	}

	@GetMapping("/getuser")
	public String getUser() {
		return tokenValue;
	}

	public static void main(String[] args) {
		SpringApplication.run(AswApplication.class, args);
	}
}

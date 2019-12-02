package com.aswg12c.demo;

import com.aswg12c.demo.controller.SessionController;
import com.aswg12c.demo.model.Session;
import com.aswg12c.demo.model.User;
import com.aswg12c.demo.repository.SessionRepository;
import com.aswg12c.demo.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.minidev.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;

@EnableJpaRepositories
@SpringBootApplication
@EnableOAuth2Sso
@RestController
//Coses del swagger
@ApiIgnore
//(value = "Usuaris", description = "Session operations")
//End of swagger
public class AswApplication extends WebSecurityConfigurerAdapter {

	public String tokenValue;
	public String userId;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionRepository sessionRepository;

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
				.and().csrf().ignoringAntMatchers("/**", "/api/**");

	}

	@RequestMapping("/user")
	public Authentication user(Authentication authentication) {
		Object t = authentication.getDetails();
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)t;
		tokenValue = details.getTokenValue();
		CreateUser();
		return authentication;
	}
	
	@RequestMapping("/logout")
	public void logout() {
		sessionRepository.deleteByUserId(userId);
		userId = "0";
		tokenValue = "0";
	}

	private void CreateUser() {
		String baseUrl = "https://graph.facebook.com/me?access_token=";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(baseUrl + tokenValue)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
				String name = json.get("name").getAsString();
				String fbid = json.get("id").getAsString();
				userId = fbid;
				try {
					User oldUser = userRepository.findByFacebookId(userId);
					if (oldUser == null) {
						User newUser = new User(name, fbid, tokenValue);
						userRepository.save(newUser);
					}
					else {
						oldUser.setFacebookToken(tokenValue);
						userRepository.save(oldUser);
					}
				} catch (Exception e) {

				}
				try {
					sessionRepository.deleteByUserId(userId);
					Session newSession = new Session(userId, tokenValue, true);
					sessionRepository.save(newSession);
				} catch (Exception e) {
					
				}
			} else {

			}
		} catch (Exception e) {

		}
	}
	//Coses del swagger
	@ApiOperation(value = "Returns current user autentication token")
	//End of swagger
	@GetMapping("/usertoken")
	public String getUser() {
		return tokenValue;
	}

	public static void main(String[] args) {
		SpringApplication.run(AswApplication.class, args);
	}
}

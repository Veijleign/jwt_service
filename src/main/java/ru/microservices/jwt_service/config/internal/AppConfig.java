package ru.microservices.jwt_service.config.internal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.TimeZone;

@Slf4j
@Configuration
public class AppConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@PostConstruct
	void initTimezone() {
		log.info("Default timezone initialized");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Bean
	public ObjectMapper objectMapper() {
		log.info("Default object mapper initialized");
		return new ObjectMapper()
				.registerModules()
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}

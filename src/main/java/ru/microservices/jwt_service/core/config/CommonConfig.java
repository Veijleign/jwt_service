package ru.microservices.jwt_service.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.TimeZone;

@Slf4j
@Configuration
@Getter
public class CommonConfig {

	@Value("${jwt.key}")
	private String key;

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

	@Bean
	public JwtDecoder jwtDecoder() {
		byte[] decode = Decoders.BASE64.decode(key);

		log.info("Jwt decoder initialized");

		return NimbusJwtDecoder.withSecretKey(
				Keys.hmacShaKeyFor(decode)
		).build();
	}

}

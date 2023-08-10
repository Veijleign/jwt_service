package ru.microservices.jwt_service.config.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "instance")
public class InstanceConfig {
	private String key;
	private String id;
}

package com.bloodlink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI bloodLinkOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("BloodLink — Blood Donor Registry and Search")
						.description("REST API for registering blood donors, searching eligible donors, "
								+ "recording blood donations, tracking donation history, and viewing blood-group statistics.")
						.version("1.0"));
	}
}
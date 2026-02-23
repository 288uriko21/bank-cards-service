package com.example.bankcards.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;



@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Bank Cards API",
        version = "v1",
        description = "Система управления банковскими картами. Сначала получите JWT через /api/auth/login и укажите его в Swagger UI (Authorize)."
    ),
    security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
	    name = "BearerAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT"
	)
public class OpenApiConfig {
}


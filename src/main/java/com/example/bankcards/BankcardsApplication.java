
package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	    info = @Info(
	        title = "Bank Cards API",
	        version = "v1",
	        description = "Система управления банковскими картами. Сначала получите JWT через /api/auth/login и укажите его в Swagger UI (Authorize)."
	    )
	)

@SpringBootApplication
public class BankcardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }
}

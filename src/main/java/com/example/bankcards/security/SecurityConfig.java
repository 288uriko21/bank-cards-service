package com.example.bankcards.security;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                    // /ping открыт всем
	                    .requestMatchers("/ping").permitAll()

	                    // только ADMIN может вызывать GET /api/cards (все карты)
	                    .requestMatchers(HttpMethod.GET, "/api/cards")
	                        .hasRole("ADMIN")

	                    // управление статусом карт — только ADMIN
	                    .requestMatchers(HttpMethod.PATCH, "/api/cards/*/block")
	                        .hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.PATCH, "/api/cards/*/activate")
	                        .hasRole("ADMIN")
	                    .requestMatchers(HttpMethod.DELETE, "/api/cards/*")
	                        .hasRole("ADMIN")

	                    // запрос блокировки своей карты — любой залогиненный
	                    .requestMatchers(HttpMethod.POST, "/api/cards/*/block-request")
	                        .authenticated()

	                    // остальные операции с картами (создание, /my и т.п.)
	                    .requestMatchers("/api/cards/**")
	                        .authenticated()

	                    // всё остальное тоже требует логина
	                    .anyRequest().authenticated()
	            )
	            .httpBasic(Customizer.withDefaults())
	            .formLogin(Customizer.withDefaults());

	    return http.build();
	}



//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder.encode("user"))
//                .roles("USER")
//                .build();
//        
//        UserDetails julia = User.withUsername("julia")
//                .password(passwordEncoder.encode("321"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user, julia);
//    }

    // 3) Как шифруются пароли
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

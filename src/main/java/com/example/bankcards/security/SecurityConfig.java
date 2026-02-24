package com.example.bankcards.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cards").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/cards/*/block").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/cards/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cards/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/cards/*/block-request").authenticated()

                        .requestMatchers("/api/cards/**").authenticated()

                        .anyRequest().authenticated()
                )
               
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



//@Bean
//public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//  UserDetails admin = User.withUsername("admin")
//          .password(passwordEncoder.encode("admin"))
//          .roles("ADMIN")
//          .build();
//
//  UserDetails user = User.withUsername("user")
//          .password(passwordEncoder.encode("user"))
//          .roles("USER")
//          .build();
//  
//  UserDetails julia = User.withUsername("julia")
//          .password(passwordEncoder.encode("321"))
//          .roles("USER")
//          .build();
//  return new InMemoryUserDetailsManager(admin, user, julia);
//}

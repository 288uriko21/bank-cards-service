package com.example.bankcards.controller;

import com.example.bankcards.dto.ChangePasswordRequest;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Аутентификация и смена пароля")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;      
    private final PasswordEncoder passwordEncoder;    
    

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;        
        this.passwordEncoder = passwordEncoder;      
    }

    @Operation(
        summary = "Логин и получение JWT",
        description = """
Возвращает JWT-токен для доступа к защищённым операциям.
Сначала вызовите этот эндпоинт, затем нажмите кнопку Authorize в Swagger UI
и вставьте значение в формате: Bearer <полученный токен>.
"""
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return new LoginResponse(token);
    }

    @Operation(
            summary = "Изменение своего пароля")
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public void changeMyPassword(@Valid @RequestBody ChangePasswordRequest request,
                                 Principal principal) {
        String username = principal.getName();

        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
   
}

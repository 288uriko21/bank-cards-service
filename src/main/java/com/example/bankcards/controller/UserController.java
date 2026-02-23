package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import java.security.SecureRandom;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление пользователями (только для администратора)")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
	

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей без паролей. Доступно только администратору.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Успешно"),
			@ApiResponse(responseCode = "401", description = "Неавторизован"),
			@ApiResponse(responseCode = "403", description = "Недостаточно прав") })
	@GetMapping
	public List<UserResponseDto> getAll() {
		return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}
	
	
    @Operation(
            summary = "Создать пользователя",
            description = "Создает нового пользователя с указанной ролью. Пароль сохраняется в зашифрованном виде."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
        })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponseDto create(@RequestBody UserCreateRequest request) {
		UserEntity user = new UserEntity();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());

		UserEntity saved = userRepository.save(user);
		return toDto(saved);
	}
    
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе без пароля по его идентификатору."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
        })
	@GetMapping("/{id}")
	public UserResponseDto getById(@PathVariable Long id) {
		UserEntity user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
		return toDto(user);
	}

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его ID."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
        })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		userRepository.deleteById(id);
	}
    
    @Operation(
    	    summary = "Сброс пароля пользователя (ADMIN)",
    	    description = "Устанавливает пользователю новый временный пароль. "
    	        + "Пароль генерируется автоматически и сохраняется в зашифрованном виде."
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Пароль успешно сброшен"),
    	    @ApiResponse(responseCode = "401", description = "Неавторизован"),
    	    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
    	    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    	})
    @PatchMapping("/{id}/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto resetPassword(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newPassword = generateTempPassword(10); 
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        System.out.println("Temporary password for user " + user.getUsername() + ": " + newPassword);

        return toDto(user);
    }

    
    private String generateTempPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

	private UserResponseDto toDto(UserEntity entity) {
		return new UserResponseDto(entity.getId(), entity.getUsername(), entity.getRole());
	}
}

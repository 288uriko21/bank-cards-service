package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.CardNumberMasker;
import com.example.bankcards.dto.TransactionHistoryItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.dto.TransactionHistoryItem;
import com.example.bankcards.exception.BusinessException;
import jakarta.validation.Valid;




import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Операции с банковскими картами")
@SecurityRequirement(name = "BearerAuth")
public class CardController {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransferService transferService; 

    public CardController(CardRepository cardRepository,
                          UserRepository userRepository,
                          TransferService transferService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.transferService = transferService;
    }
    
    @Operation(
            summary = "Получить все карты (ADMIN)",
            description = "Возвращает список всех карт в системе."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
        })
	@GetMapping
	public List<CardResponseDto> getAll() {
		return cardRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}
    
    @Operation(
            summary = "Мои карты",
            description = "Возвращает карты текущего аутентифицированного пользователя."
        )
	@GetMapping("/my")
	public List<CardResponseDto> getMyCards(java.security.Principal principal) {
		String username = principal.getName();

		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		return cardRepository.findByOwner(user).stream().map(this::toDto).collect(Collectors.toList());
	}
	
    @Operation(
            summary = "Транзакции пользователя с заданным id (ADMIN)"
        )
	@GetMapping("/{id}/transfers")
	public List<TransactionHistoryItem> getCardTransactions(@PathVariable Long id,
	                                                        java.security.Principal principal) {
	    String username = principal.getName();
	    return transferService.getCardTransactions(username, id);
	}

    @Operation(
    	    summary = "Создать банковскую карту",
    	    description = """
    	Создает новую карту и возвращает её данные с замаскированным номером. 
    	Администратор может создать карту для любого пользователя, передав его ID в поле userId. 
    	Обычный пользователь всегда создаёт карту только для себя — даже если в запросе указан другой userId, будет использован текущий аутентифицированный пользователь. 
    	Номер карты должен быть строкой из ровно 16 цифр; при попытке использовать уже существующий номер будет возвращена бизнес-ошибка.
    	"""
    	)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CardResponseDto create(@Valid @RequestBody CardCreateRequest request, java.security.Principal principal) {

		String username = principal.getName();

		UserEntity currentUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Current user not found"));


		boolean isAdmin = "admin".equals(currentUser.getUsername());

		UserEntity owner;
		if (isAdmin) {
			owner = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("User not found"));
		} else {
			owner = currentUser;
		}
		
		if (cardRepository.existsByCardNumber(request.getCardNumber())) {
	        throw new BusinessException("Card with this number already exists");
	    }

		CardEntity card = new CardEntity();
		card.setCardNumber(request.getCardNumber());
		card.setExpiryDate(request.getExpiryDate());
		card.setStatus(CardStatus.ACTIVE);
		card.setBalance(request.getInitialBalance());
		card.setOwner(owner);

		CardEntity saved = cardRepository.save(card);
		return toDto(saved);
	}
	
    @Operation(
    	    summary = "Запрос на блокировку собственной карты",
    	    description = "Карта с id помечается BLOCKED"

    	)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешно"),
        @ApiResponse(responseCode = "401", description = "Неавторизован"),
        @ApiResponse(responseCode = "404", description = "Карта или пользователь не найдены")
    })
	@PostMapping("/{id}/block-request")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto requestBlock(@PathVariable Long id,
	                                    java.security.Principal principal) {
	    String username = principal.getName();

	    UserEntity user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new IllegalArgumentException("User not found"));

	    CardEntity card = cardRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Card not found"));

	    if (!card.getOwner().getId().equals(user.getId())) {
	        throw new IllegalArgumentException("You can block only your own cards");
	    }

	    card.setStatus(CardStatus.BLOCKED);
	    CardEntity saved = cardRepository.save(card);
	    return toDto(saved);
	}

	 @Operation(
	    	    summary = "Запрос на блокировку любой карты (ADMIN)",
	    	    description = "Карта с id помечается BLOCKED"

	    	)
	@PatchMapping("/{id}/block")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto blockCard(@PathVariable Long id) {
		CardEntity card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));

		card.setStatus(CardStatus.BLOCKED);
		CardEntity saved = cardRepository.save(card);
		return toDto(saved);
	}

	 @Operation(
	    	    summary = "Запрос на активацию любой карты (ADMIN)",
	    	    description = "Карта с id помечается ACTIVE"

	    	)
	@PatchMapping("/{id}/activate")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto activateCard(@PathVariable Long id) {
		CardEntity card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));

		card.setStatus(CardStatus.ACTIVE);
		CardEntity saved = cardRepository.save(card);
		return toDto(saved);
	}

	 @Operation(
	    	    summary = "Запрос на удаление любой карты (ADMIN)",
	    	    description = "Карта с id помечается DELETED"

	    	)
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCard(@PathVariable Long id) {
		CardEntity card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));

		card.setStatus(CardStatus.DELETED);
		cardRepository.save(card);
	}

	private CardResponseDto toDto(CardEntity entity) {
		return new CardResponseDto(entity.getId(), CardNumberMasker.mask(entity.getCardNumber()),
				entity.getExpiryDate(), entity.getStatus().name(), entity.getBalance(), entity.getOwner().getId(),
				entity.getOwner().getUsername());
	}
}

package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberMasker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {

	private final CardRepository cardRepository;
	private final UserRepository userRepository;

	public CardController(CardRepository cardRepository, UserRepository userRepository) {
		this.cardRepository = cardRepository;
		this.userRepository = userRepository;
	}

	// Получить все карты (для админа; доступ ограничен в SecurityConfig)
	@GetMapping
	public List<CardResponseDto> getAll() {
		return cardRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@GetMapping("/my")
	public List<CardResponseDto> getMyCards(java.security.Principal principal) {
		String username = principal.getName();

		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		return cardRepository.findByOwner(user).stream().map(this::toDto).collect(Collectors.toList());
	}

	// Создать карту: админ может для любого userId, обычный пользователь — только
	// для себя
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CardResponseDto create(@RequestBody CardCreateRequest request, java.security.Principal principal) {

		String username = principal.getName();

		UserEntity currentUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Current user not found"));

		// ВРЕМЕННО: считаем админом пользователя с username "admin"
		// (позже заменим на нормальные роли из БД)
		boolean isAdmin = "admin".equals(currentUser.getUsername());

		UserEntity owner;
		if (isAdmin) {
			// админ может создать карту любому пользователю из запроса
			owner = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("User not found"));
		} else {
			// обычный пользователь всегда создаёт карту только себе,
			// даже если в запросе передан другой userId
			owner = currentUser;
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
	
	@PostMapping("/{id}/block-request")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto requestBlock(@PathVariable Long id,
	                                    java.security.Principal principal) {
	    String username = principal.getName();

	    UserEntity user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new IllegalArgumentException("User not found"));

	    CardEntity card = cardRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Card not found"));

	    // пользователь может трогать только свои карты
	    if (!card.getOwner().getId().equals(user.getId())) {
	        throw new IllegalArgumentException("You can block only your own cards");
	    }

	    // по ТЗ: «запрашивает блокировку карты» — можно сразу ставить BLOCKED
	    card.setStatus(CardStatus.BLOCKED);
	    CardEntity saved = cardRepository.save(card);
	    return toDto(saved);
	}


	// Заблокировать карту (ADMIN)
	@PatchMapping("/{id}/block")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto blockCard(@PathVariable Long id) {
		CardEntity card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));

		card.setStatus(CardStatus.BLOCKED);
		CardEntity saved = cardRepository.save(card);
		return toDto(saved);
	}

	// Активировать карту (ADMIN)
	@PatchMapping("/{id}/activate")
	@ResponseStatus(HttpStatus.OK)
	public CardResponseDto activateCard(@PathVariable Long id) {
		CardEntity card = cardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Card not found"));

		card.setStatus(CardStatus.ACTIVE);
		CardEntity saved = cardRepository.save(card);
		return toDto(saved);
	}

	// Мягкое удаление карты (ADMIN)
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

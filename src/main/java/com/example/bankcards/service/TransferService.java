package com.example.bankcards.service;

import com.example.bankcards.dto.ExternalTransferRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.dto.TransactionHistoryItem;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TransferService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private static final BigDecimal DAILY_LIMIT = new BigDecimal("10000.00");

    public TransferService(CardRepository cardRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transferBetweenOwnCards(String username, TransferRequest request) {
        CardEntity from = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new IllegalArgumentException("From card not found"));

        CardEntity to = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new IllegalArgumentException("To card not found"));

        // находим пользователя по username вместо userId
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Проверка, что обе карты принадлежат этому пользователю
        if (!from.getOwner().getId().equals(user.getId()) ||
            !to.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cards must belong to the same user");
        }

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Both cards must be ACTIVE");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);

        TransactionEntity tx = new TransactionEntity(
                from,
                to,
                amount,
                "SUCCESS",
                "Transfer completed",
                "INTERNAL"
        );
        transactionRepository.save(tx);


        return new TransferResponse(
                tx.getId(),
                tx.getStatus(),
                tx.getMessage(),
                from.getBalance(),
                to.getBalance()
        );
    }
    
    @Transactional(readOnly = true)
    public List<TransactionHistoryItem> getMyTransfers(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TransactionEntity> txs = transactionRepository.findByFromCardOwner(user);

        return txs.stream()
                .map(tx -> new TransactionHistoryItem(
                        tx.getId(),
                        tx.getFromCard().getId(),
                        tx.getToCard().getId(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getMessage(),
                        tx.getCreatedAt()
                ))
                .toList();
    }
    
    @Transactional(noRollbackFor = BusinessException.class)
    public TransferResponse transferExternal(String username, ExternalTransferRequest request) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        CardEntity from = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new IllegalArgumentException("From card not found"));

        CardEntity to = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new IllegalArgumentException("To card not found"));

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // USER может списывать только со своих карт, ADMIN — с любых
        if (!isAdmin && !from.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can transfer only from your own cards");
        }

        // Проверка статусов карт
        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Both cards must be ACTIVE");
        }

        // Суточный лимит только для EXTERNAL‑переводов обычного пользователя
        if (!isAdmin) {
            LocalDate today = LocalDate.now();
            LocalDateTime fromTime = today.atStartOfDay();
            LocalDateTime toTime = today.atTime(LocalTime.MAX);

            BigDecimal usedToday = transactionRepository
                    .sumSuccessfulExternalAmountByOwnerAndCreatedAtBetween(user, fromTime, toTime);

            BigDecimal newTotal = usedToday.add(amount);
            if (newTotal.compareTo(DAILY_LIMIT) > 0) {
                TransactionEntity failedTx = new TransactionEntity(
                        from,
                        to,
                        amount,
                        "FAILED",
                        "Daily limit exceeded",
                        "EXTERNAL"
                );
                transactionRepository.save(failedTx);

                throw new BusinessException("Daily limit exceeded");
            }
        }

        // Проверка баланса
        if (from.getBalance().compareTo(amount) < 0) {
            TransactionEntity failedTx = new TransactionEntity(
                    from,
                    to,
                    amount,
                    "FAILED",
                    "Not enough funds",
                    "EXTERNAL"
            );
            transactionRepository.save(failedTx);

            throw new BusinessException("Not enough funds");
        }

        // Успешный перевод
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);

        TransactionEntity tx = new TransactionEntity(
                from,
                to,
                amount,
                "SUCCESS",
                "External transfer completed",
                "EXTERNAL"
        );
        transactionRepository.save(tx);

        return new TransferResponse(
                tx.getId(),
                tx.getStatus(),
                tx.getMessage(),
                from.getBalance(),
                to.getBalance()
        );
    }


}

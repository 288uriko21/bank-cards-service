package com.example.bankcards.repository;

import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.CardEntity;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;


public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByFromCardOwner(UserEntity owner);

    @Query("""
    	       select coalesce(sum(t.amount), 0)
    	       from TransactionEntity t
    	       where t.fromCard.owner = :owner
    	         and t.status = 'SUCCESS'
    	         and t.type = 'EXTERNAL'
    	         and t.createdAt between :from and :to
    	       """)
    	BigDecimal sumSuccessfulExternalAmountByOwnerAndCreatedAtBetween(
    	        @Param("owner") UserEntity owner,
    	        @Param("from") LocalDateTime from,
    	        @Param("to") LocalDateTime to);

    List<TransactionEntity> findByFromCardOrToCard(CardEntity fromCard, CardEntity toCard);
}



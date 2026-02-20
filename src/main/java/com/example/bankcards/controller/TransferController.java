package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransactionHistoryItem;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.dto.ExternalTransferRequest;
import com.example.bankcards.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.*;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Переводы средств и история операций")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    
    @Operation(
            summary = "Внутренний перевод между своими картами",
            description = """
    Перевод средств между двумя картами текущего пользователя.
    Обе карты должны принадлежать текущему пользователю и иметь статус ACTIVE.
    При недостатке средств или неверных данных создаётся FAILED-транзакция и возвращается бизнес-ошибка.
    """
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Бизнес-ошибка (недостаточно средств, неверные карты и т.п.)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransferResponse transfer(@RequestBody TransferRequest request,
                                     java.security.Principal principal) {
        String username = principal.getName(); 

        return transferService.transferBetweenOwnCards(username, request);
    }
    
    @Operation(
            summary = "Моя история переводов",
            description = "Возвращает список операций, в которых текущий пользователь является отправителем (fromCard.owner)."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История успешно получена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        })
    @GetMapping("/my")
    public List<TransactionHistoryItem> getMyTransfers(java.security.Principal principal) {
        String username = principal.getName();
        return transferService.getMyTransfers(username);
    }
    
    @Operation(
            summary = "Внешний перевод на любую карту",
            description = """
    Перевод с карты текущего пользователя на любую другую карту.
    Для USER действует суточный лимит по успешным внешним операциям; при его превышении или недостатке средств создаётся FAILED-транзакция и возвращается бизнес-ошибка.
    Для ADMIN лимит не применяется и можно переводить между любыми картами.
    """
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Бизнес-ошибка (лимит, недостаточно средств, чужая карта и т.п.)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        })
    @PostMapping("/external")
    @ResponseStatus(HttpStatus.OK)
    public TransferResponse externalTransfer(@RequestBody ExternalTransferRequest request,
                                             java.security.Principal principal) {
        String username = principal.getName();
        return transferService.transferExternal(username, request);
    }

}

package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransactionHistoryItem;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.dto.ExternalTransferRequest;
import com.example.bankcards.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransferResponse transfer(@RequestBody TransferRequest request,
                                     java.security.Principal principal) {
        String username = principal.getName(); 

        return transferService.transferBetweenOwnCards(username, request);
    }
    
    @GetMapping("/my")
    public List<TransactionHistoryItem> getMyTransfers(java.security.Principal principal) {
        String username = principal.getName();
        return transferService.getMyTransfers(username);
    }
    
    @PostMapping("/external")
    @ResponseStatus(HttpStatus.OK)
    public TransferResponse externalTransfer(@RequestBody ExternalTransferRequest request,
                                             java.security.Principal principal) {
        String username = principal.getName();
        return transferService.transferExternal(username, request);
    }

}

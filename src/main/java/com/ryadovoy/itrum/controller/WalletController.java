package com.ryadovoy.itrum.controller;

import com.ryadovoy.itrum.dto.request.WalletOperationRequest;
import com.ryadovoy.itrum.dto.response.WalletResponse;
import com.ryadovoy.itrum.service.WalletService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/{walletId}")
    public WalletResponse findWallet(@PathVariable UUID walletId) {
        return walletService.findWalletById(walletId);
    }

    @PostMapping
    public WalletResponse updateBalance(@RequestBody @Valid WalletOperationRequest request) {
        return walletService.processWalletOperation(
                request.walletId(),
                request.operationType(),
                request.amount()
        );
    }
}

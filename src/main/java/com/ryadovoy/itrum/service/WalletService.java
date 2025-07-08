package com.ryadovoy.itrum.service;

import com.ryadovoy.itrum.dto.response.WalletResponse;
import com.ryadovoy.itrum.model.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {
    WalletResponse findWalletById(UUID walletId);
    WalletResponse processWalletOperation(UUID walletId, OperationType operationType, BigDecimal amount);
}

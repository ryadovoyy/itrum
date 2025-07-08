package com.ryadovoy.itrum.dto.request;

import com.ryadovoy.itrum.model.OperationType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletOperationRequest(
        @NotNull(message = "Wallet ID is required")
        UUID walletId,

        @NotNull(message = "Operation type (DEPOSIT or WITHDRAW) is required")
        OperationType operationType,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        @Digits(integer = 17, fraction = 2, message = "Amount must have up to 17 integer digits and 2 decimal places")
        BigDecimal amount
) {
}

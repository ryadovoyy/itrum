package com.ryadovoy.itrum.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID walletId, BigDecimal currentBalance, BigDecimal amount) {
        super(String.format(
                "Insufficient funds in wallet %s. Current balance: %.2f, attempted to withdraw: %.2f",
                walletId, currentBalance, amount
        ));
    }
}

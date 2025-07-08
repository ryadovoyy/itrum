package com.ryadovoy.itrum.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletBalanceLimitExceededException extends RuntimeException {
    public WalletBalanceLimitExceededException(UUID walletId, BigDecimal currentBalance, BigDecimal amount) {
        super(String.format(
                "Balance limit exceeded in wallet %s. Current balance: %.2f, attempted to deposit: %.2f",
                walletId, currentBalance, amount
        ));
    }
}

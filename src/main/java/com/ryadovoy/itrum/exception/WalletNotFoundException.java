package com.ryadovoy.itrum.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID walletId) {
        super("Wallet with id " + walletId + " not found");
    }
}

package com.ryadovoy.itrum.service.impl;

import com.ryadovoy.itrum.dto.response.WalletResponse;
import com.ryadovoy.itrum.exception.InsufficientFundsException;
import com.ryadovoy.itrum.exception.WalletBalanceLimitExceededException;
import com.ryadovoy.itrum.exception.WalletNotFoundException;
import com.ryadovoy.itrum.model.OperationType;
import com.ryadovoy.itrum.model.Wallet;
import com.ryadovoy.itrum.repository.WalletRepository;
import com.ryadovoy.itrum.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private static final BigDecimal MAX_BALANCE = new BigDecimal("99999999999999999.99");

    private final WalletRepository walletRepository;

    @Value("${app.wallet.max-overdraft}")
    private BigDecimal maxOverdraft;

    @Override
    @Transactional(readOnly = true)
    public WalletResponse findWalletById(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    @Override
    @Transactional
    public WalletResponse processWalletOperation(UUID walletId, OperationType operationType, BigDecimal amount) {
        Wallet wallet = walletRepository.findAndLockById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        BigDecimal currentBalance = wallet.getBalance();

        BigDecimal newBalance = operationType == OperationType.DEPOSIT
                ? currentBalance.add(amount)
                : currentBalance.subtract(amount);

        if (newBalance.compareTo(maxOverdraft) < 0) {
            throw new InsufficientFundsException(walletId, currentBalance, amount);
        }

        if (newBalance.compareTo(MAX_BALANCE) > 0) {
            throw new WalletBalanceLimitExceededException(walletId, currentBalance, amount);
        }

        log.info(
                "Attempting to update wallet {} balance from {} to {} (operation: {}, amount: {})",
                walletId, currentBalance, newBalance, operationType, amount
        );

        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        log.info(
                "Successfully updated wallet {} balance to {} (operation: {}, amount: {})",
                walletId, savedWallet.getBalance(), operationType, amount
        );

        return new WalletResponse(savedWallet.getId(), savedWallet.getBalance());
    }
}

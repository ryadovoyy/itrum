package com.ryadovoy.itrum.controller;

import com.ryadovoy.itrum.dto.request.WalletOperationRequest;
import com.ryadovoy.itrum.dto.response.WalletResponse;
import com.ryadovoy.itrum.model.OperationType;
import com.ryadovoy.itrum.model.Wallet;
import com.ryadovoy.itrum.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletControllerTestIT {
    private static final UUID NON_EXISTENT_WALLET_ID = UUID.randomUUID();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;

    @BeforeEach
    void setUp() {
        Wallet wallet = new Wallet();
        Wallet savedWallet = walletRepository.save(wallet);
        walletId = savedWallet.getId();
    }

    @Test
    void findWallet_whenWalletExists_returnsWallet() {
        ResponseEntity<WalletResponse> response = restTemplate.getForEntity(
                "/api/v1/wallets/" + walletId, WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().walletId()).isEqualTo(walletId);
        assertThat(response.getBody().balance()).isEqualTo("0.00");
    }

    @Test
    void findWallet_whenWalletNotExists_returnsNotFound() {
        ResponseEntity<Object> response = restTemplate.getForEntity(
                "/api/v1/wallets/" + NON_EXISTENT_WALLET_ID, Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.01",
            "1",
            "99999999999999999.99"
    })
    void updateBalance_whenDeposit_updatesBalance(BigDecimal amount) {
        WalletOperationRequest request = new WalletOperationRequest(walletId, OperationType.DEPOSIT, amount);

        ResponseEntity<WalletResponse> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().walletId()).isEqualTo(walletId);
        assertThat(response.getBody().balance()).isEqualTo(String.format("%.2f", amount));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0.01",
            "1",
            "50.00"
    })
    void updateBalance_whenWithdraw_updatesBalance(BigDecimal amount) {
        WalletOperationRequest request = new WalletOperationRequest(walletId, OperationType.WITHDRAW, amount);

        ResponseEntity<WalletResponse> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().walletId()).isEqualTo(walletId);
        assertThat(response.getBody().balance()).isEqualTo(String.format("-%.2f", amount));
    }

    @ParameterizedTest
    @CsvSource({
            ",,",
            ",DEPOSIT,",
            ",,1",
            ",DEPOSIT,1",
            "6fe7f92f-d729-47ba-b5b6-c77803a7c679,,",
            "6fe7f92f-d729-47ba-b5b6-c77803a7c679,DEPOSIT,",
            "6fe7f92f-d729-47ba-b5b6-c77803a7c679,,1",
            "6fe7f92f-d729-47ba-b5b6-c77803a7c679,DEPOSIT,0.001",
            "6fe7f92f-d729-47ba-b5b6-c77803a7c679,DEPOSIT,100000000000000000.00",
    })
    void updateBalance_whenInvalidRequest_returnsBadRequest(UUID walletId, OperationType operationType, BigDecimal amount) {
        WalletOperationRequest request = new WalletOperationRequest(walletId, operationType, amount);

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateBalance_whenWalletNotExists_returnsNotFound() {
        WalletOperationRequest request = new WalletOperationRequest(NON_EXISTENT_WALLET_ID, OperationType.DEPOSIT, BigDecimal.ONE);

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateBalance_whenInsufficientFunds_returnsConflict() {
        WalletOperationRequest request = new WalletOperationRequest(walletId, OperationType.WITHDRAW, new BigDecimal("50.01"));

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).extracting("detail").asString().startsWith("Insufficient funds");
    }

    @Test
    void updateBalance_whenLimitExceeded_returnsConflict() {
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("99999999999999999.99"));
        Wallet savedWallet = walletRepository.save(wallet);

        WalletOperationRequest request = new WalletOperationRequest(savedWallet.getId(), OperationType.DEPOSIT, new BigDecimal("0.01"));

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/v1/wallets", request, Object.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).extracting("detail").asString().startsWith("Balance limit exceeded");
    }
}

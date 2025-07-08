package com.ryadovoy.itrum.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
        UUID walletId,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal balance
) {
}

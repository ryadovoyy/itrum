package com.ryadovoy.itrum.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ValidationError(
        String field,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Object rejectedValue,

        String message
) {
}

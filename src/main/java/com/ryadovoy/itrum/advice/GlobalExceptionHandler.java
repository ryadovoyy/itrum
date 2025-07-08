package com.ryadovoy.itrum.advice;

import com.ryadovoy.itrum.dto.response.ValidationError;
import com.ryadovoy.itrum.exception.InsufficientFundsException;
import com.ryadovoy.itrum.exception.WalletBalanceLimitExceededException;
import com.ryadovoy.itrum.exception.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        log.info(ex.getMessage());
        List<ValidationError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors.add(
                    new ValidationError(
                            error.getField(),
                            error.getRejectedValue(),
                            error.getDefaultMessage()
                    )
            );
        });

        ProblemDetail body = createProblemDetail(ex, status, "Validation failed", null, null, request);
        body.setProperty("field-errors", validationErrors);

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Object> handleWalletNotFoundException(WalletNotFoundException ex, WebRequest request) {
        log.info(ex.getMessage());
        return handleCustomException(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            InsufficientFundsException.class,
            WalletBalanceLimitExceededException.class
    })
    public ResponseEntity<Object> handleWalletBalanceException(Exception ex, WebRequest request) {
        log.info(ex.getMessage());
        return handleCustomException(ex, request, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> handleCustomException(Exception ex, WebRequest request, HttpStatusCode status) {
        ProblemDetail body = createProblemDetail(ex, status, ex.getMessage(), null, null, request);
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }
}

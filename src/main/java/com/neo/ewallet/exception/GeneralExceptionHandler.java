package com.neo.ewallet.exception;

import ch.qos.logback.classic.Logger;
import com.neo.ewallet.dto.ApiError;
import com.neo.ewallet.dto.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TransactionResponse handleIllegalArgument(IllegalArgumentException ex) {
        return TransactionResponse.error(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TransactionResponse handleBusinessException(BusinessException ex) {
        return TransactionResponse.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public TransactionResponse handleGenericException(Exception ex) {
        // log full stacktrace internally
        return TransactionResponse.error("Internal server error");
    }
}

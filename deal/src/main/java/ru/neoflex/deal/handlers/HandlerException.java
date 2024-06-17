package ru.neoflex.deal.handlers;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class HandlerException {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> FeignException(FeignException exception) {

        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.contentUTF8());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handlerException(Exception ex) {

        final List<AppError> message = List.of(
                new AppError("Error", ex.getMessage()));

        log.error(ex.getMessage());
        return new ApiErrorResponse(message);
    }
}

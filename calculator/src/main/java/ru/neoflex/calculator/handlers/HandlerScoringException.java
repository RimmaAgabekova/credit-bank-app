package ru.neoflex.calculator.handlers;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.neoflex.calculator.exceptions.ScoringException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class HandlerScoringException {
    @ExceptionHandler({ScoringException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handlerException(Exception ex) {

        final List<AppError> message = List.of(
                new AppError("Error", ex.getMessage()));

        log.error(ex.getMessage());
        return new ApiErrorResponse(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handlerException(MethodArgumentNotValidException ex) {

        final List<AppError> message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new AppError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.error(ex.getMessage());
        return new ApiErrorResponse(message);
    }

}

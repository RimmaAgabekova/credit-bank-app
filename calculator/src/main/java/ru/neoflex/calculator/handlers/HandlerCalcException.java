package ru.neoflex.calculator.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.neoflex.calculator.exceptions.CalcException;

@RestControllerAdvice
public class HandlerCalcException {
    @ExceptionHandler(CalcException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CalcException handleCalcException(CalcException ex) {

        return new CalcException(ex.getMessage());
    }

}

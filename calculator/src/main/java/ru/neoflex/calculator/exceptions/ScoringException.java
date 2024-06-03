package ru.neoflex.calculator.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ScoringException extends RuntimeException{
    public ScoringException(String message){
        super(message);
    }


}

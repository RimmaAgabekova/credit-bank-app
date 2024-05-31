package ru.neoflex.calculator.exceptions;

public class CalcException extends RuntimeException{
    public  CalcException(String message){
        super(message,null,false,false);
    }
}

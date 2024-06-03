package ru.neoflex.calculator.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppError {
    private String name;
    private String message;

}

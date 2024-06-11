package ru.neoflex.deal.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppError {
    private String name;
    private String message;
}

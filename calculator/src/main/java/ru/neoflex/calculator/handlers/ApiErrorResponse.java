package ru.neoflex.calculator.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor

public class ApiErrorResponse {

    private final List<AppError> apiErrorsResponse;
}

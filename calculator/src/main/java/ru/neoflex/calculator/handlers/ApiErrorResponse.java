package ru.neoflex.calculator.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ApiErrorResponse {

    private final List<AppError> apiErrorsResponse;

}

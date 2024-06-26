package ru.neoflex.calculator.handlers;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private List<AppError> apiErrorsResponse;

}

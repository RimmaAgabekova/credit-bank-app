package ru.neoflex.calculator.handlers;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppError {
    private String name;
    private String message;
}

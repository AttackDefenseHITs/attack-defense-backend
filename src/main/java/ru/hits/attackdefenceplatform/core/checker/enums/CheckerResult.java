package ru.hits.attackdefenceplatform.core.checker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckerResult {
    OK(101),
    CORRUPT(102),
    MUMBLE(103),
    DOWN(104);

    private final int code;
}

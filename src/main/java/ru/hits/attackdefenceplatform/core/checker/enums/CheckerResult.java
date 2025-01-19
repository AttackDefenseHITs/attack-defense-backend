package ru.hits.attackdefenceplatform.core.checker.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckerResult {
    OK(101),
    CORRUPT(102),
    MUMBLE(103),
    DOWN(104),
    CHECK_FAILED(110);

    private final int code;

    public int toCode() {
        return this.code;
    }

    public static CheckerResult fromCode(int code) {
        for (CheckerResult result : values()) {
            if (result.code == code) {
                return result;
            }
        }
        return CHECK_FAILED;
    }
}


package ru.hits.attackdefenceplatform.common.exception.flag;

public class FlagExpiredException extends RuntimeException {
    public FlagExpiredException(String message) {
        super(message);
    }
}

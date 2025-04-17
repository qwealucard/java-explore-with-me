package ru.practicum.exception;

public class ImpossibleActionException extends RuntimeException {
    public ImpossibleActionException(String message) {
        super(message);
    }
}

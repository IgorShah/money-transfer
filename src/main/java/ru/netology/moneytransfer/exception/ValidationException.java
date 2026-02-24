package ru.netology.moneytransfer.exception;

public class ValidationException extends RuntimeException {
    private final int id;

    public ValidationException(String message, int id) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

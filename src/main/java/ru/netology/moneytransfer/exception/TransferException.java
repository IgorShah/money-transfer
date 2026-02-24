package ru.netology.moneytransfer.exception;

public class TransferException extends RuntimeException {
    private final int id;

    public TransferException(String message, int id) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

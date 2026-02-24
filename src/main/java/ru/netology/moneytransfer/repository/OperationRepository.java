package ru.netology.moneytransfer.repository;

import ru.netology.moneytransfer.model.PendingOperation;

public interface OperationRepository {
    void save(PendingOperation operation);

    PendingOperation findById(String id);

    void remove(String id);
}

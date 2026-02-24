package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.PendingOperation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOperationRepository implements OperationRepository {
    private final Map<String, PendingOperation> operations = new ConcurrentHashMap<>();

    @Override
    public void save(PendingOperation operation) {
        operations.put(operation.getId(), operation);
    }

    @Override
    public PendingOperation takeById(String id) {
        return operations.remove(id);
    }
}

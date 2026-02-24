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
    public PendingOperation findById(String id) {
        return operations.get(id);
    }

    @Override
    public void remove(String id) {
        operations.remove(id);
    }
}

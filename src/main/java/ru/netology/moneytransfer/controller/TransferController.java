package ru.netology.moneytransfer.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.moneytransfer.dto.ConfirmRequest;
import ru.netology.moneytransfer.dto.OperationResponse;
import ru.netology.moneytransfer.dto.TransferRequest;
import ru.netology.moneytransfer.service.TransferService;

@RestController
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public OperationResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    @PostMapping("/confirmOperation")
    public OperationResponse confirm(@Valid @RequestBody ConfirmRequest request) {
        return transferService.confirm(request);
    }
}

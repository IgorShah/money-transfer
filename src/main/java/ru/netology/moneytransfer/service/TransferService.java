package ru.netology.moneytransfer.service;

import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.ConfirmRequest;
import ru.netology.moneytransfer.dto.OperationResponse;
import ru.netology.moneytransfer.dto.TransferRequest;
import ru.netology.moneytransfer.exception.TransferException;
import ru.netology.moneytransfer.exception.ValidationException;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.PendingOperation;
import ru.netology.moneytransfer.repository.CardRepository;
import ru.netology.moneytransfer.repository.OperationRepository;

import java.util.UUID;

@Service
public class TransferService {
    private static final String CONFIRMATION_CODE = "0000";

    private final CardRepository cardRepository;
    private final OperationRepository operationRepository;
    private final TransferLogService logService;

    public TransferService(CardRepository cardRepository,
                           OperationRepository operationRepository,
                           TransferLogService logService) {
        this.cardRepository = cardRepository;
        this.operationRepository = operationRepository;
        this.logService = logService;
    }

    public OperationResponse transfer(TransferRequest request) {
        if (request.getAmount() == null) {
            throw new ValidationException("Amount is required", 40001);
        }

        if (request.getCardFromNumber().equals(request.getCardToNumber())) {
            throw new ValidationException("Source and destination cards must be different", 40002);
        }

        Card fromCard = cardRepository.findByNumber(request.getCardFromNumber());
        if (fromCard == null) {
            throw new ValidationException("Source card not found", 40003);
        }

        Card toCard = cardRepository.findByNumber(request.getCardToNumber());
        if (toCard == null) {
            throw new ValidationException("Destination card not found", 40004);
        }

        validateCardData(fromCard, request.getCardFromValidTill(), request.getCardFromCVV());

        long amount = request.getAmount().getValue();
        long commission = calculateCommission(amount);
        long totalDebit = amount + commission;

        if (fromCard.getBalanceInMinorUnits() < totalDebit) {
            logService.write(fromCard.getNumber(), toCard.getNumber(), amount, commission, request.getAmount().getCurrency(), "DECLINED: insufficient funds");
            throw new TransferException("Insufficient funds", 50001);
        }

        String operationId = UUID.randomUUID().toString();
        PendingOperation operation = new PendingOperation(
                operationId,
                fromCard.getNumber(),
                toCard.getNumber(),
                amount,
                commission,
                request.getAmount().getCurrency()
        );
        operationRepository.save(operation);
        logService.write(fromCard.getNumber(), toCard.getNumber(), amount, commission, request.getAmount().getCurrency(), "PENDING");
        return new OperationResponse(operationId);
    }

    public OperationResponse confirm(ConfirmRequest request) {
        PendingOperation operation = operationRepository.findById(request.getOperationId());
        if (operation == null) {
            throw new ValidationException("Operation not found", 40005);
        }

        if (!CONFIRMATION_CODE.equals(request.getCode())) {
            logService.write(operation.getFromCard(), operation.getToCard(), operation.getAmount(), operation.getCommission(), operation.getCurrency(), "DECLINED: invalid confirmation code");
            throw new TransferException("Invalid confirmation code", 50002);
        }

        Card fromCard = cardRepository.findByNumber(operation.getFromCard());
        if (fromCard == null) {
            throw new TransferException("Source card missing during confirmation", 50003);
        }

        Card toCard = cardRepository.findByNumber(operation.getToCard());
        if (toCard == null) {
            throw new TransferException("Destination card missing during confirmation", 50004);
        }

        long totalDebit = operation.getAmount() + operation.getCommission();
        if (fromCard.getBalanceInMinorUnits() < totalDebit) {
            logService.write(operation.getFromCard(), operation.getToCard(), operation.getAmount(), operation.getCommission(), operation.getCurrency(), "DECLINED: insufficient funds at confirmation");
            throw new TransferException("Insufficient funds", 50005);
        }

        fromCard.debit(totalDebit);
        toCard.credit(operation.getAmount());
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        operationRepository.remove(operation.getId());

        logService.write(operation.getFromCard(), operation.getToCard(), operation.getAmount(), operation.getCommission(), operation.getCurrency(), "CONFIRMED");
        return new OperationResponse(operation.getId());
    }

    private long calculateCommission(long amount) {
        return Math.max(1L, Math.round(amount * 0.01));
    }

    private void validateCardData(Card card, String validTill, String cvv) {
        if (!card.getValidTill().equals(validTill) || !card.getCvv().equals(cvv)) {
            throw new ValidationException("Card credentials are invalid", 40006);
        }
    }
}

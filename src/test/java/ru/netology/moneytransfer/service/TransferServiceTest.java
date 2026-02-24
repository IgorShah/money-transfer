package ru.netology.moneytransfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.moneytransfer.dto.AmountDto;
import ru.netology.moneytransfer.dto.ConfirmRequest;
import ru.netology.moneytransfer.dto.OperationResponse;
import ru.netology.moneytransfer.dto.TransferRequest;
import ru.netology.moneytransfer.exception.TransferException;
import ru.netology.moneytransfer.exception.ValidationException;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.PendingOperation;
import ru.netology.moneytransfer.repository.CardRepository;
import ru.netology.moneytransfer.repository.OperationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private TransferLogService logService;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(cardRepository, operationRepository, logService);
    }

    @Test
    void shouldCreatePendingOperationOnTransfer() {
        Card from = new Card("1111222233334444", "12/29", "123", 100_000);
        Card to = new Card("5555666677778888", "11/28", "456", 100_000);
        TransferRequest request = new TransferRequest(
                from.getNumber(),
                from.getValidTill(),
                from.getCvv(),
                to.getNumber(),
                new AmountDto(10_000, "RUR")
        );

        when(cardRepository.findByNumber(from.getNumber())).thenReturn(from);
        when(cardRepository.findByNumber(to.getNumber())).thenReturn(to);

        OperationResponse response = transferService.transfer(request);

        assertEquals(false, response.getOperationId().isBlank());
        verify(operationRepository).save(any(PendingOperation.class));
        verify(logService).write(from.getNumber(), to.getNumber(), 10_000, 100, "RUR", "PENDING");
    }

    @Test
    void shouldConfirmOperationAndTransferMoney() {
        Card from = new Card("1111222233334444", "12/29", "123", 100_000);
        Card to = new Card("5555666677778888", "11/28", "456", 20_000);
        PendingOperation operation = new PendingOperation("op-1", from.getNumber(), to.getNumber(), 10_000, 100, "RUR");

        when(operationRepository.takeById("op-1")).thenReturn(operation).thenReturn(null);
        when(cardRepository.findByNumber(from.getNumber())).thenReturn(from);
        when(cardRepository.findByNumber(to.getNumber())).thenReturn(to);

        OperationResponse response = transferService.confirm(new ConfirmRequest("op-1", "0000"));

        assertEquals("op-1", response.getOperationId());
        assertEquals(89_900, from.getBalanceInMinorUnits());
        assertEquals(30_000, to.getBalanceInMinorUnits());
        verify(logService).write(from.getNumber(), to.getNumber(), 10_000, 100, "RUR", "CONFIRMED");

        assertThrows(ValidationException.class, () -> transferService.confirm(new ConfirmRequest("op-1", "0000")));
        verify(operationRepository, times(2)).takeById("op-1");
    }

    @Test
    void shouldThrowWhenInvalidConfirmationCode() {
        PendingOperation operation = new PendingOperation("op-1", "1111222233334444", "5555666677778888", 10_000, 100, "RUR");
        when(operationRepository.takeById("op-1")).thenReturn(operation);

        assertThrows(TransferException.class, () -> transferService.confirm(new ConfirmRequest("op-1", "9999")));

        verify(operationRepository).takeById("op-1");
    }

    @Test
    void shouldThrowWhenCardCredentialsInvalid() {
        Card from = new Card("1111222233334444", "12/29", "123", 100_000);
        Card to = new Card("5555666677778888", "11/28", "456", 100_000);

        when(cardRepository.findByNumber(from.getNumber())).thenReturn(from);
        when(cardRepository.findByNumber(to.getNumber())).thenReturn(to);

        TransferRequest request = new TransferRequest(
                from.getNumber(),
                "01/30",
                "123",
                to.getNumber(),
                new AmountDto(10_000, "RUR")
        );

        assertThrows(ValidationException.class, () -> transferService.transfer(request));
    }
}

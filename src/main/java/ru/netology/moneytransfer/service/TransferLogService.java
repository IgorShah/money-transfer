package ru.netology.moneytransfer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TransferLogService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path logPath;

    public TransferLogService(@Value("${transfer.log.path:logs/transfers.log}") String logPath) {
        this.logPath = Paths.get(logPath);
    }

    public synchronized void write(String fromCard, String toCard, long amount, long commission, String currency, String result) {
        try {
            Path parent = logPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String entry = String.format(
                    "%s | from=%s | to=%s | amount=%d %s | commission=%d %s | result=%s%n",
                    LocalDateTime.now().format(FORMATTER),
                    maskCard(fromCard),
                    maskCard(toCard),
                    amount,
                    currency,
                    commission,
                    currency,
                    result
            );
            Files.writeString(logPath, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write transfer log", e);
        }
    }

    private String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return cardNumber.substring(0, 4) + "********" + cardNumber.substring(cardNumber.length() - 4);
    }
}

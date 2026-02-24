package ru.netology.moneytransfer.model;

public class PendingOperation {
    private final String id;
    private final String fromCard;
    private final String toCard;
    private final long amount;
    private final long commission;
    private final String currency;

    public PendingOperation(String id, String fromCard, String toCard, long amount, long commission, String currency) {
        this.id = id;
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.commission = commission;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getFromCard() {
        return fromCard;
    }

    public String getToCard() {
        return toCard;
    }

    public long getAmount() {
        return amount;
    }

    public long getCommission() {
        return commission;
    }

    public String getCurrency() {
        return currency;
    }
}

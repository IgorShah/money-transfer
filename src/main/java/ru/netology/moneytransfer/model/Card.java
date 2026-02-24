package ru.netology.moneytransfer.model;

import java.util.Objects;

public class Card {
    private final String number;
    private final String validTill;
    private final String cvv;
    private long balanceInMinorUnits;

    public Card(String number, String validTill, String cvv, long balanceInMinorUnits) {
        this.number = number;
        this.validTill = validTill;
        this.cvv = cvv;
        this.balanceInMinorUnits = balanceInMinorUnits;
    }

    public String getNumber() {
        return number;
    }

    public String getValidTill() {
        return validTill;
    }

    public String getCvv() {
        return cvv;
    }

    public long getBalanceInMinorUnits() {
        return balanceInMinorUnits;
    }

    public void debit(long amount) {
        this.balanceInMinorUnits -= amount;
    }

    public void credit(long amount) {
        this.balanceInMinorUnits += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(number, card.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}

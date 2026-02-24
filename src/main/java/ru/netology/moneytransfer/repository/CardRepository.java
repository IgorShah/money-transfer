package ru.netology.moneytransfer.repository;

import ru.netology.moneytransfer.model.Card;

public interface CardRepository {
    Card findByNumber(String number);

    void save(Card card);
}

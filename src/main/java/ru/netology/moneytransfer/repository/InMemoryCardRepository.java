package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.Card;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCardRepository implements CardRepository {
    private final Map<String, Card> cards = new ConcurrentHashMap<>();

    public InMemoryCardRepository() {
        cards.put("1111222233334444", new Card("1111222233334444", "12/29", "123", 100_000_000));
        cards.put("5555666677778888", new Card("5555666677778888", "11/28", "456", 100_000_000));
    }

    @Override
    public Card findByNumber(String number) {
        return cards.get(number);
    }

    @Override
    public void save(Card card) {
        cards.put(card.getNumber(), card);
    }
}

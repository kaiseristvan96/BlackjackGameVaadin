package inf.unideb.hu.backend.util;

import inf.unideb.hu.backend.entity.CardEntity;

import java.util.ArrayList;
import java.util.Collections;

public class GenerateDeckUtil {
    private ArrayList<CardEntity> deck = new ArrayList<CardEntity>();

    public ArrayList<CardEntity> getDeck() {
        return deck;
    }

    //return the specified card from the deck
    public CardEntity getSingleCardFromDeck(int index) {
        if (index >= 0 && index < deck.size()) {
            return deck.remove(index);
        } else {
            //If the index is out of bounds return the first element...
            return deck.remove(0);
        }
    }

    public GenerateDeckUtil() {
        for (int i = 1; i < 14; i++) {
            CardEntity card = new CardEntity(i,'H');
            deck.add(card);
        }

        for (int i = 1; i < 14; i++) {
            CardEntity card = new CardEntity(i,'D');
            deck.add(card);
        }

        for (int i = 1; i < 14; i++) {
            CardEntity card = new CardEntity(i,'C');
            deck.add(card);
        }

        for (int i = 1; i < 14; i++) {
            CardEntity card = new CardEntity(i,'S');
            deck.add(card);
        }
        Collections.shuffle(deck);
    }


    @Override
    public String toString() {
        for (int i = 0; i < deck.size(); i++) {
            System.out.println(deck.get(i).getNumber() + " of " + deck.get(i).getSuit());
        }
        return "";
    }


}

package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CardModel implements Comparable<CardModel> {

    public CardModel(String cardName, String cardPrice, String cardId) {
        this.cardName = cardName;
        this.cardPrice = cardPrice;
        this.cardId = cardId;
    }

    private String cardName;
    private String cardPrice;
    private String cardId;
    private Double cardPriceValueUsd;
    private Double cardPriceValueEuro;
    private Double cardPriceValueLei;

    @Override
    public int compareTo(CardModel o) {
        return this.getCardPriceValueEuro().compareTo(o.getCardPriceValueEuro());
    }

}

package service;

import model.CardModel;
import model.Currency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import static constants.ThreeNineConstants.*;

public class AmountConverter {


    private List<Currency> getAmounts() {

        List<Currency> listOfCurrency = new LinkedList<>();

        Document exchangePage = null;
        final String ES_ELEMENT_CURS_BOX_ID = "#cursBox";
        final String ES_ELEMENT_CURS_BOX_TABLE = "table";
        final String ES_ELEMENT_CURRENCY_CLASS = ".currency";
        final String ES_ELEMENT_RATE_CLASS = ".rate";
        final String ES_ELEMENT_TR = "tr";

        try {

            exchangePage = Jsoup.connect(EXCHANGE_SOURCE).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exchangePage != null) {
            Element cursBox = exchangePage.body().selectFirst(ES_ELEMENT_CURS_BOX_ID).selectFirst(ES_ELEMENT_CURS_BOX_TABLE);
            cursBox.select(ES_ELEMENT_TR).forEach(tr -> {
                if (tr != null && (tr.selectFirst(ES_ELEMENT_CURRENCY_CLASS).text().equals("USD") || tr.selectFirst(ES_ELEMENT_CURRENCY_CLASS).text().equals("EUR"))) {
                    listOfCurrency.add(new Currency(tr.selectFirst(ES_ELEMENT_CURRENCY_CLASS).text(), convertCurrencyToDouble(tr.selectFirst(ES_ELEMENT_RATE_CLASS).text())));
                }

            });
        }
        return listOfCurrency;
    }

    public List<CardModel> setAmounts(List<CardModel> listOfCards) {
        List<Currency> listOfCurrency = getAmounts();
        listOfCards.forEach(card -> {

            if (card.getCardPrice().contains(USD)) {
                card.setCardPriceValueUsd(convertCardCurrencyToDouble(card.getCardPrice()));
                card.setCardPriceValueEuro(convertToEuro(convertCardCurrencyToDouble(card.getCardPrice()), USD, listOfCurrency));
                card.setCardPriceValueLei(convertToLei(convertCardCurrencyToDouble(card.getCardPrice()), USD, listOfCurrency));
            } else if (card.getCardPrice().contains(EURO)) {
                card.setCardPriceValueEuro(convertCardCurrencyToDouble(card.getCardPrice()));
                card.setCardPriceValueUsd(convertToUsd(convertCardCurrencyToDouble(card.getCardPrice()), EURO, listOfCurrency));
                card.setCardPriceValueLei(convertToLei(convertCardCurrencyToDouble(card.getCardPrice()), EURO, listOfCurrency));
            } else {
                card.setCardPriceValueLei(convertCardCurrencyToDouble(card.getCardPrice()));
                card.setCardPriceValueUsd(convertToUsd(convertCardCurrencyToDouble(card.getCardPrice()), LEI_RU, listOfCurrency));
                card.setCardPriceValueEuro(convertToEuro(convertCardCurrencyToDouble(card.getCardPrice()), LEI_RU, listOfCurrency));
            }
        });

        return listOfCards;
    }

    private Double convertCurrencyToDouble(String currency) {
        currency = currency.replace(",", ".");
        return Double.valueOf(currency.substring(0, 7));
    }

    private Double convertCardCurrencyToDouble(String cardCurrency) {
        if (cardCurrency.contains(EURO)) {
            cardCurrency = cardCurrency.substring(0, cardCurrency.indexOf(EURO)).replaceAll("\\s+", "");
        } else if (cardCurrency.contains(USD)) {
            cardCurrency = cardCurrency.substring(0, cardCurrency.indexOf(USD)).replaceAll("\\s+", "");
        } else if (cardCurrency.contains(LEI_RU)) {
            cardCurrency = cardCurrency.substring(0, cardCurrency.indexOf(LEI_RU)).replaceAll("\\s+", "");
        } else if (cardCurrency.contains(LEI_RO)) {
            cardCurrency = cardCurrency.substring(0, cardCurrency.indexOf(LEI_RO)).replaceAll("\\s+", "");
        }
        return Double.parseDouble(cardCurrency);
    }

    private Double convertToUsd(Double cardPrice, String key, List<Currency> currencyList) {
        Double oneEuroLei = 1.0;
        Double oneUsdLei = 1.0;

        for (Currency currency : currencyList) {
            if (currency.getCurrencyName().equals("EUR")) {
                oneEuroLei = currency.getRate();
            } else {
                oneUsdLei = currency.getRate();
            }
        }
        double response = (key.equals(EURO)) ? oneEuroLei * cardPrice / oneUsdLei : cardPrice / oneUsdLei;
        return BigDecimal.valueOf(response).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private Double convertToEuro(Double cardPrice, String key, List<Currency> currencyList) {
        Double oneEuroLei = 1.0;
        Double oneUsdLei = 1.0;

        for (Currency currency : currencyList) {
            if (currency.getCurrencyName().equals("USD")) {
                oneUsdLei = currency.getRate();
            } else {
                oneEuroLei = currency.getRate();
            }
        }
        double response = (key.equals(USD)) ? oneUsdLei * cardPrice / oneEuroLei : cardPrice / oneEuroLei;
        return BigDecimal.valueOf(response).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private Double convertToLei(Double cardPrice, String key, List<Currency> currencyList) {
        Double oneEuroLei = 1.0;
        Double oneUsdLei = 1.0;

        for (Currency currency : currencyList) {
            if (currency.getCurrencyName().equals("USD")) {
                oneUsdLei = currency.getRate();
            } else {
                oneEuroLei = currency.getRate();
            }
        }
        double response = (key.equals(EURO)) ? cardPrice * oneEuroLei : cardPrice * oneUsdLei;
        return BigDecimal.valueOf(response).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}

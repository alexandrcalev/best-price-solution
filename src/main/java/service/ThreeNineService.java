package service;

import model.CardModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import template.CardObtainService;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static constants.ThreeNineConstants.*;

public class ThreeNineService implements CardObtainService {

    private StringBuilder title;

    public ThreeNineService(String title) {
        this.title = new StringBuilder(title);
    }

    @Override
    public List<CardModel> getAllCardsWithPerPages(int startPage, int endPage) {

        List<CardModel> listOfAllCardsPerPage = new LinkedList<>();

        for (int i = startPage; i <= endPage; i++) {
            title.append(PER_PAGE_KEY_WORD).append(i);
            listOfAllCardsPerPage.addAll(getAllFoundedCards(title.toString()));
        }

        return listOfAllCardsPerPage;
    }

    private List<CardModel> getAllFoundedCards(String title) {

        List<CardModel> listOfCards = new LinkedList<>();

        Document threeNineDocument = getConnectToTitle(title);

        if (threeNineDocument != null) {
            threeNineDocument.body().selectFirst(TABLE_OF_CARDS_ID).select(CARD_ROW_ELEMENT_CLASS).forEach(element -> {
                if (element.selectFirst(CARD_TITLE_CLASS) != null && !containADS(element)) {
                    listOfCards.add(cardMaker(element));
                }
            });
        }

        return listOfCards;
    }

    private Document getConnectToTitle(String title) {
        try {
            return Jsoup.connect(title).get();
        } catch (IOException e) {
            e.fillInStackTrace();
            e.getMessage();
            return null;
        }
    }

    private boolean containADS(Element element) {
        return element.selectFirst(CARD_TITLE_CLASS).selectFirst(CARD_LINK_ELEMENT).attributes().get(CARD_LINK_ELEMENT_ATTRIBUTE).contains(ADD_KEY_WORD);
    }

    private CardModel cardMaker(Element element) {
        CardModel cardModel = new CardModel();
        cardModel.setCardName(element.selectFirst(CARD_TITLE_CLASS).selectFirst(CARD_LINK_ELEMENT).text());
        String cardPrice = (!element.selectFirst(CARD_PRICE_CLASS).text().toLowerCase().contains(WRONG_PRICE_RU) && !element.selectFirst(CARD_PRICE_CLASS).text().toLowerCase().contains(WRONG_PRICE_RO)) ? element.selectFirst(CARD_PRICE_CLASS).text() : "0.0";
        cardModel.setCardPrice(cardPrice);
        cardModel.setCardId(element.selectFirst(CARD_TITLE_CLASS).selectFirst(CARD_LINK_ELEMENT).attributes().get(CARD_LINK_ELEMENT_ATTRIBUTE));
        return cardModel;
    }
}

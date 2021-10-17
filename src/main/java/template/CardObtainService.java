package template;

import model.CardModel;

import java.util.List;

public interface CardObtainService {
    List<CardModel> getAllCardsWithPerPages(int start, int end);
}

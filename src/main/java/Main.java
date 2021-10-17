import model.CardModel;
import service.AmountConverter;
import service.ExcelReportService;
import service.ThreeNineService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Input link : ");
        Scanner scanner = new Scanner(System.in);
        System.out.print("link : ");
        String title = scanner.next();
        ThreeNineService threeNineService = new ThreeNineService(title);
        List<CardModel> listOfCards = threeNineService.getAllCardsWithPerPages(1, 3);
        new AmountConverter().setAmounts(listOfCards);
        new ExcelReportService().writeCards(listOfCards.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
    }
}

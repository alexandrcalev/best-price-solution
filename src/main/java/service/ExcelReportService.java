package service;

import model.CardModel;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import static constants.ThreeNineConstants.*;

public class ExcelReportService {
    private final String REPORT_PATH = "C:\\Users\\Alexandr\\Desktop";
    private final String REPORT_NAME = "threeNineReport.xlsx";

    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet("Three Nine Report in Java");
    CreationHelper helper = workbook.getCreationHelper();

    public void writeCards(Set<CardModel> listOfCards) {

        int rowNum = 0;
        System.out.println("Creating excel");

        for (CardModel datatype : listOfCards) {
            Row row = sheet.createRow(rowNum++);
            createCellsForRow(row, datatype);
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(REPORT_PATH + "\\" + REPORT_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

    private void createCellsForRow(Row row, CardModel card) {
        row.createCell(0).setCellValue(card.getCardName());
        row.createCell(1).setCellValue(card.getCardPrice());
        row.createCell(2).setCellValue(card.getCardPriceValueEuro() + EURO);
        row.createCell(3).setCellValue(card.getCardPriceValueUsd() + USD);
        row.createCell(4).setCellValue(card.getCardPriceValueLei() + LEI_RU);
        String cardAddress = ThreeNineTitle + card.getCardId();
        row.createCell(5).setCellValue(cardAddress);
        createHyperLinkForCell(row.getCell(5),cardAddress);
    }

    private void createHyperLinkForCell(Cell cellLink, String cellAddress) {
        XSSFHyperlink link = new XSSFHyperlink(helper.createHyperlink(HyperlinkType.URL));
        XSSFCell cell = (XSSFCell) cellLink;
        link.setAddress(cellAddress);
        cell.setHyperlink(link);
    }
}

package template;

import java.util.List;

public interface ReportService {
    void createFile();
    void setTitle(String ...args);
    void setData(List listOfCellData);
}

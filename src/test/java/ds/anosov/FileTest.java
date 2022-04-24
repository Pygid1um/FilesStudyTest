package ds.anosov;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;

public class FileTest {

    String testPdfFile = "TestPDF.pdf";
    String testXlsxFile = "TestXLSX.xlsx";
    String testCsvFile = "TestCSV.csv";

    ClassLoader classLoader = FileTest.class.getClassLoader();

    @DisplayName("Парсинг Zip архива")
    @Test
    void readPdfFile() throws Exception {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/ZipFiles/Desktop.zip"));
        ZipInputStream zipInputStream = new ZipInputStream(Objects.requireNonNull(classLoader.getResourceAsStream("ZipFiles/Desktop.zip")));
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {

                if (entry.getName().equals(testPdfFile)) {
                    PDF pdf = new PDF(inputStream);
                    Assertions.assertEquals(59, pdf.numberOfPages);
                    assertThat(pdf,  new ContainsExactText("Windows"));
                    System.out.println("PDF файл содержит " + pdf.numberOfPages + " страниц.");
                }

                if (entry.getName().equals(testXlsxFile)) {
                    XLS xls = new XLS(inputStream);
                    String cellValue = xls.excel.getSheetAt(0).getRow(7).getCell(1).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(cellValue).contains("Etta");
                }

                if (entry.getName().equals(testCsvFile)) {
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    List<String[]> content = reader.readAll();
                    org.assertj.core.api.Assertions.assertThat(content).contains(
                            new String[]{"John", "Doe", "120 jefferson st.", "Riverside", " NJ", " 08075"},
                            new String[]{"Jack", "McGinnis", "220 hobo Av.", "Phila", " PA", "09119"},
                            new String[]{"John \"Da Man\"", "Repici", "120 Jefferson St.", "Riverside", " NJ", "08075"}

                        );

                }

            }
        }
        System.out.println("Ваши тесты работают. Вы прекрасны :)");
    }
}

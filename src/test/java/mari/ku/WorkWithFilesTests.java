package mari.ku;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.qameta.allure.*;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static mari.ku.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkWithFilesTests extends TestBase {

    TestData testData = new TestData();

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Загрузка файлов")
    @Story("Загрузка текстовых файлов")
    @DisplayName("Проверка успешной загрузки TXT файла")
    void txtFileUploadTest() {
        open(TXT_FILE_UPLOAD_PAGE);
        $("#pc-upload-add").uploadFromClasspath(TXT_FILE_NAME_UPLOAD);
        $(".dt-file-name-inner").shouldHave(text((TXT_FILE_NAME_UPLOAD)));
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Загрузка файлов")
    @Story("Загрузка графических файлов")
    @DisplayName("Проверка успешной загрузки JPG файла")
    void jpgFileUploadTest() {
        open(JPG_FILE_UPLOAD_PAGE);
        $("#pc-upload-add").uploadFromClasspath(JPG_FILE_NAME);
        $(".dt-file-name-inner").shouldHave(text((JPG_FILE_NAME)));
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Скачивание файлов")
    @Story("Скачивание текстовых файлов")
    @DisplayName("Скачивание TXT файла и проверка его содержимого")
    void txtFileDownloadAndCheckTest() throws IOException {
        open(TXT_FILE_DOWNLOAD_PAGE);
        File txtFile = $$("strong").findBy(text(TXT_FILE_NAME_DOWNLOAD)).parent().sibling(0).download();
        String txtContent = IOUtils.toString((new FileReader((txtFile))));
        assertTrue(txtContent.contains(TXT_FILE_CONTENT));
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Скачивание файлов")
    @Story("Скачивание текстовых файлов")
    @DisplayName("Скачивание DOCX файла и проверка его содержимого")
    void docxFileDownloadAndCheckTest() throws IOException, InvalidFormatException {
        open(DOCX_FILE_DOWNLOAD_PAGE);
        File docxFile = $$("strong").findBy(text(DOCX_FILE_NAME)).parent().sibling(0).download();
        XWPFDocument docxFileDoc = new XWPFDocument(OPCPackage.open(docxFile));
        XWPFWordExtractor extractor = new XWPFWordExtractor(docxFileDoc);
        assertTrue(extractor.getText().contains(DOCX_FILE_CONTENT));
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Скачивание файлов")
    @Story("Скачивание текстовых файлов")
    @DisplayName("Скачивание PDF файла, проверка его автора и количество страниц")
    void pdfFileDownloadAndCheckTest() throws IOException {
        open(PDF_FILE_DOWNLOAD_PAGE);
        File pdfFile = $$("strong").findBy(text(PDF_FILE_NAME)).parent().sibling(0).download();
        PDF parsePdf = new PDF(pdfFile);
        assertEquals(PDF_FILE_AUTHOR, parsePdf.author);
        assertEquals(20, parsePdf.numberOfPages);
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Скачивание файлов")
    @Story("Скачивание текстовых файлов")
    @DisplayName("Скачивание XLS файла и проверка его содержимого")
    void xlsFileDownloadAndCheckTest() throws IOException {
        open(XLS_FILE_DOWNLOAD_PAGE);
        File xlsFile = $$("strong").findBy(text(XLS_FILE_NAME)).parent().sibling(0).download();
        XLS parseXls = new XLS(xlsFile);
        boolean checkPassed = parseXls.excel
                .getSheetAt(1)
                .getRow(19)
                .getCell(1)
                .getStringCellValue()
                .contains(XLS_FILE_CONTENT);
        assertTrue(checkPassed);
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Скачивание файлов")
    @Story("Скачивание текстовых файлов")
    @DisplayName("Скачивание XLSX файла и проверка его содержимого")
    void xlsxFileDownloadAndCheckTest() throws IOException {
        open(XLSX_FILE_DOWNLOAD_PAGE);
        File xlsxFile = $$("strong").findBy(text(XLSX_FILE_NAME)).parent().sibling(0).download();
        XLS parseXlsx = new XLS(xlsxFile);
        boolean checkPassed = parseXlsx.excel
                .getSheetAt(0)
                .getRow(0)
                .getCell(3)
                .getStringCellValue()
                .contains(XLSX_FILE_CONTENT);
        assertTrue(checkPassed);
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Работа с готовыми файлами")
    @Story("Парсим файлы")
    @DisplayName("Парсим CSV файл, проверяем количество полей у него")
    void csvFileParseAndCheckTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(CSV_FILE_NAME);
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> strings = csvReader.readAll();
            assertEquals(15, strings.size());
        }
    }

    @Test
    @Owner("marina-ku4erenko")
    @Feature("Работа с готовыми файлами")
    @Story("Парсим файлы")
    @DisplayName("Парсим ZIP файл, проверяем количество вложений")
    void zipFileParseAndCheckTest() throws IOException {
        ClassLoader classLoader1 = this.getClass().getClassLoader();
        try (InputStream is = classLoader1.getResourceAsStream(ZIP_FILE_NAME);
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            int count = 0;
            while ((entry = zis.getNextEntry()) != null) count++;
            assertEquals(3, count);
        }
    }
}



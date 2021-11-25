package com.moneystats.MoneyStats.databaseExportTest.templateTest;

import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseJSONExportDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseException;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateException;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SpringBootTest
public class TemplateServiceTest {

  @Autowired TemplateService templateService;
  @Captor ArgumentCaptor<DatabaseJSONExportDTO> jsonExportDTOArgumentCaptor;
  @Captor ArgumentCaptor<String> filePathCaptor;
/*
  @Test
  void testGetTemplate() throws TemplateException, FileNotFoundException {
    String templateId = TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE;

    File sqlExportTemplate = new File(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    Scanner scanner = new Scanner(sqlExportTemplate);
    List<String> alltemplateLine = new ArrayList<>();
    while (scanner.hasNextLine()) {
      alltemplateLine.add(scanner.nextLine());
    }
    TemplateDTO expectedTemplateDTO = createValidTemplateDTO(sqlExportTemplate, alltemplateLine);

    TemplateDTO actualTemplateDTO = templateService.getTemplate(templateId);

    Assertions.assertEquals(expectedTemplateDTO.getTemplate(), actualTemplateDTO.getTemplate());
    Assertions.assertEquals(expectedTemplateDTO.getContent(), actualTemplateDTO.getContent());
  }*/
/*
  @Test
  void testApplyTemplate() throws TemplateException, FileNotFoundException {
    File sqlExportTemplate = new File(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    Scanner scanner = new Scanner(sqlExportTemplate);
    List<String> alltemplateLine = new ArrayList<>();
    while (scanner.hasNextLine()) {
      alltemplateLine.add(scanner.nextLine());
    }
    TemplateDTO expectedTemplateDTO = createValidTemplateDTO(sqlExportTemplate, alltemplateLine);
    Map<String, List<String>> placeholders = Map.of("key", List.of("value"));
    Map<String, String> placeholderTemplate = Map.of("key", "value");

    List<String> applyTemplate =
        templateService.applyTemplate(expectedTemplateDTO, placeholders, placeholderTemplate);
    Assertions.assertTrue(applyTemplate != null);
  }

  @Test
  void testSaveTemplate() throws TemplateException, FileNotFoundException {
    File sqlExportTemplate = new File(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    Scanner scanner = new Scanner(sqlExportTemplate);
    List<String> alltemplateLine = new ArrayList<>();
    while (scanner.hasNextLine()) {
      alltemplateLine.add(scanner.nextLine());
    }
    TemplateDTO expectedTemplateDTO = createValidTemplateDTO(sqlExportTemplate, alltemplateLine);
    Map<String, List<String>> placeholders = Map.of("key", List.of("value"));
    Map<String, String> placeholderTemplate = Map.of("key", "value");

    List<String> applyTemplate =
        templateService.applyTemplate(expectedTemplateDTO, placeholders, placeholderTemplate);
    Assertions.assertTrue(applyTemplate != null);

    String filePath = TemplatePlaceholders.FILEPATH_BACKUP;
    templateService.saveTemplate(filePath, applyTemplate);
  }*/

  @Test
  void testApplyAndSaveJsonBackup() throws DatabaseException {
    DatabaseJSONExportDTO databaseJSONExportDTO = createValidDatabaseJSONDTO();
    String filePath = TemplatePlaceholders.FILEPATH_BACKUP;

    templateService.applyAndSaveJsonBackup(databaseJSONExportDTO, filePath);
  }

  private DatabaseJSONExportDTO createValidDatabaseJSONDTO() {
    return new DatabaseJSONExportDTO(null, List.of(), List.of(), List.of());
  }
/*
  private TemplateDTO createValidTemplateDTO(File sqlExportTemplate, List<String> alltemplateLine) {
    return new TemplateDTO(alltemplateLine, null, sqlExportTemplate);
  }*/
}

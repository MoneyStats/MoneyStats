package com.moneystats.MoneyStats.databaseExportTest.templateTest;

import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateException;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
public class TemplateServiceTest {

    @Autowired TemplateService templateService;

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
  }

  private TemplateDTO createValidTemplateDTO(File sqlExportTemplate, List<String> alltemplateLine) {
    return new TemplateDTO(alltemplateLine, null, sqlExportTemplate);
  }
}

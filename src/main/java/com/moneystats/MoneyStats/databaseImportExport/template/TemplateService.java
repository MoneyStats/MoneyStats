package com.moneystats.MoneyStats.databaseImportExport.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseJSONExportDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseException;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

@Service
public class TemplateService {

  private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);
  private static ObjectMapper objectMapper = new ObjectMapper();

  private MustacheFactory mustacheFactory = new DefaultMustacheFactory();
  private Map<String, Mustache> cache = new HashMap<>();

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public TemplateDTO getTemplate(String templateId) throws TemplateException {
    File sqlExportTemplate = new File(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    Scanner scanner = null;
    try {
      scanner = new Scanner(sqlExportTemplate);
    } catch (FileNotFoundException e) {
      LOG.error(
          "Template not found on TemplateService getTemplate:27 {}",
          TemplateException.Code.TEMPLATE_NOT_FOUND.toString());
      throw new TemplateException(TemplateException.Code.TEMPLATE_NOT_FOUND);
    }
    List<String> alltemplateLine = new ArrayList<>();
    while (scanner.hasNextLine()) {
      alltemplateLine.add(scanner.nextLine());
    }
    scanner.close();
    return new TemplateDTO(alltemplateLine, null, sqlExportTemplate);
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public List<String> applyTemplate(
      TemplateDTO template,
      Map<String, List<String>> placeholders,
      Map<String, String> placeholderTemplate)
      throws TemplateException {
    List<String> templateList = template.getContent();
    for (int i = 0; i < templateList.size(); i++) {
      if (placeholders.containsKey(templateList.get(i))) {
        StringBuilder allInOne = new StringBuilder();
        List<String> placeholder = placeholders.get(templateList.get(i));
        for (int y = 0; y < placeholder.size(); y++) {
          allInOne.append(placeholder.get(y));
        }
        templateList.set(i, allInOne.toString());
      }
    }
    // Set Data and Database Information
    return compileTemplate(templateList, placeholderTemplate);
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public void saveTemplate(String filePath, List<String> templateList) throws TemplateException {
    String filePathToSave = filePath + "MoneyStats_Backup" + LocalDate.now().toString() + ".sql";
    File outputTemplate = new File(filePathToSave);
    BufferedWriter newTemplate;
    try {
      newTemplate = new BufferedWriter(new FileWriter(outputTemplate));
      for (String s : templateList) {
        newTemplate.write(s + TemplatePlaceholders.FIX_TEXT);
      }
      newTemplate.close();
    } catch (IOException e) {
      LOG.error(
          "it's not possible to write the new template, on TemplateService, saveTemplate:70 {}",
          TemplateException.Code.IMPOSSIBLE_TO_WRITE_THE_TEMPLATE);
      throw new TemplateException(TemplateException.Code.IMPOSSIBLE_TO_WRITE_THE_TEMPLATE);
    }
    LOG.info("Database successfully exported, PATH: {}", filePathToSave);
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public void applyAndSaveJsonBackup(DatabaseJSONExportDTO databaseJSONToExport, String filePath)
          throws DatabaseException, TemplateException {
    String filePathToSave = filePath + "json_dump_backup_" + LocalDate.now().toString() + ".backup";
    try {
      objectMapper.writeValue(new File(filePathToSave), databaseJSONToExport);
    } catch (JsonProcessingException e) {
      LOG.error(
          "A problem occurred during Serialize Object on applyAndSaveJsonBackup:132 {}",
          DatabaseException.Code.ERROR_ON_EXPORT_DATABASE.toString());
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
    } catch (IOException e) {
      LOG.error(
              "A problem occurred during Serialize Object on applyAndSaveJsonBackup:132 {}",
              DatabaseException.Code.ERROR_ON_EXPORT_DATABASE.toString());
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
    }
    LOG.info("Database successfully exported, PATH: {}", filePathToSave);
  }

  private List<String> compileTemplate(List<String> template, Map<String, String> placeholder)
      throws TemplateException {
    List<String> compiledTemplate = new ArrayList<>();
    try {
      StringWriter writer = new StringWriter();
      for (int i = 0; i < template.size(); i++) {
        int finalI = i;
        Mustache mustache =
            cache.computeIfAbsent(
                template.get(i),
                k ->
                    mustacheFactory.compile(
                        new StringReader(template.get(finalI) + TemplatePlaceholders.FIX_TEXT),
                        null));
        mustache.execute(writer, placeholder).flush();
      }
      compiledTemplate.add(writer.toString());
    } catch (IOException e) {
      String message = "Cannot fill the template";
      LOG.error(message, e);
      throw new TemplateException(TemplateException.Code.IMPOSSIBLE_TO_WRITE_THE_TEMPLATE);
    } catch (Exception e) {
      String message = "Could not compile template";
      LOG.error(message, e);
      throw new TemplateException(TemplateException.Code.IMPOSSIBLE_TO_WRITE_THE_TEMPLATE);
    }
    return compiledTemplate;
  }
}

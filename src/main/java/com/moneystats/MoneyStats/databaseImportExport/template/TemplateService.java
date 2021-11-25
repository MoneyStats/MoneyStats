package com.moneystats.MoneyStats.databaseImportExport.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseJSONExportDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseException;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.entity.TemplateEntity;
import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateService {

  @Autowired ITemplateDAO templateDAO;
  private static final Logger LOG = LoggerFactory.getLogger(TemplateService.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private MustacheFactory mustacheFactory = new DefaultMustacheFactory();
  private Map<String, Mustache> cache = new HashMap<>();

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public TemplateDTO getTemplate(String templateId) throws TemplateException {
    TemplateEntity templateEntity = templateDAO.findTemplateEntityByIdentifier(templateId);
    if (templateEntity == null) {
      LOG.error(
          "Template not found on TemplateService getTemplate:38 {}",
          TemplateException.Code.TEMPLATE_NOT_FOUND);
      throw new TemplateException(TemplateException.Code.TEMPLATE_NOT_FOUND);
    }

    LOG.info("Template String {}", templateEntity.getTemplate());
    return new TemplateDTO(
        templateEntity.getIdentifier(),
        templateEntity.getTemplate(),
        templateEntity.getDatabaseCommand());
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public TemplateDTO applyTemplate(
      TemplateDTO template,
      Map<String, String> placeholderTemplate)
      throws TemplateException {
    StringBuilder templateToApply = new StringBuilder();
    templateToApply.append(template.getTemplateContent());
    // Set Data and Database Information
    StringBuilder compiledPlaceholder =
        compilePlaceholder(templateToApply, placeholderTemplate);
    TemplateDTO appliedTemplate =
        new TemplateDTO(
            template.getIdentifier(),
            compiledPlaceholder.toString(),
            template.getDatabaseCommand());
    LOG.info("Template Applied {}", appliedTemplate.getTemplateContent());
    return appliedTemplate;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public void saveTemplate(String filePath, TemplateDTO template) throws TemplateException {
    String newFolder = filePath + LocalDate.now() + "/";
    File theDir = new File(newFolder);
    if (!theDir.exists()) {
      theDir.mkdirs();
    }
    String filePathToSave = newFolder + "MoneyStats_Backup_" + LocalDate.now() + ".sql";
    File outputTemplate = new File(filePathToSave);
    BufferedWriter newTemplate;
    try {
      newTemplate = new BufferedWriter(new FileWriter(outputTemplate));
      newTemplate.write(template.getTemplateContent());
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
      throws DatabaseException {
    String newFolder = filePath + LocalDate.now() + "/";
    File theDir = new File(newFolder);
    if (!theDir.exists()) {
      theDir.mkdirs();
    }
    String filePathToSave = newFolder + "json_dump_backup_" + LocalDate.now() + ".backup";
    try {
      objectMapper.writeValue(new File(filePathToSave), databaseJSONToExport);
    } catch (JsonProcessingException e) {
      LOG.error(
          "A problem occurred during Serialize Object on applyAndSaveJsonBackup:132 {}",
          DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
    } catch (IOException e) {
      LOG.error(
          "A problem occurred during Serialize Object on applyAndSaveJsonBackup:132 {}",
          DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_EXPORT_DATABASE);
    }
    LOG.info("Database successfully exported, PATH: {}", filePathToSave);
  }

  private StringBuilder compilePlaceholder(
      StringBuilder temp, Map<String, String> placeholder)
      throws TemplateException {
    String template = temp.toString();
    StringBuilder compiledTemplate = new StringBuilder();
    try {
      StringWriter writer = new StringWriter();
      Mustache mustache =
          cache.computeIfAbsent(
              template,
              k ->
                  mustacheFactory.compile(
                      new StringReader(template + TemplatePlaceholders.FIX_TEXT), null));
      mustache.execute(writer, placeholder).flush();
      compiledTemplate.append(writer.toString().replace("[", "").replace("]", ""));
      writer.close();
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

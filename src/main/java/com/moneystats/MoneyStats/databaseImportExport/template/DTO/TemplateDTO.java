package com.moneystats.MoneyStats.databaseImportExport.template.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TemplateDTO implements Serializable {

  @NotNull private String identifier;
  @NotNull private String templateContent;
  @NotNull private String databaseCommand;

  public TemplateDTO(String identifier, String templateContent, String databaseCommand) {
    this.identifier = identifier;
    this.templateContent = templateContent;
    this.databaseCommand = databaseCommand;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getTemplateContent() {
    return templateContent;
  }

  public void setTemplateContent(String templateContent) {
    this.templateContent = templateContent;
  }

  public String getDatabaseCommand() {
    return databaseCommand;
  }

  public void setDatabaseCommand(String databaseCommand) {
    this.databaseCommand = databaseCommand;
  }
}

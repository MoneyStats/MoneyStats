package com.moneystats.MoneyStats.databaseImportExport.template.DTO;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TemplateDTO implements Serializable {

  @NotNull private List<String> content;
  @NotNull private Map<String, List<String>> metadata;
  @NotNull private File template;

  public TemplateDTO(List<String> content, Map<String, List<String>> metadata, File template) {
    this.content = content;
    this.metadata = metadata;
    this.template = template;
  }

  public List<String> getContent() {
    return content;
  }

  public void setContent(List<String> content) {
    this.content = content;
  }

  public Map<String, List<String>> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, List<String>> metadata) {
    this.metadata = metadata;
  }

  public File getTemplate() {
    return template;
  }

  public void setTemplate(File template) {
    this.template = template;
  }
}

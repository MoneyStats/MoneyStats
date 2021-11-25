package com.moneystats.MoneyStats.databaseImportExport.template.entity;

import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.generic.dao.GenericEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TEMPLATES")
public class TemplateEntity extends GenericEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "IDENTIFIER", nullable = false)
  private String identifier;

  @Column(name = "TEMPLATE", nullable = false, length = 10000)
  private String template;

  @Column(name = "COMMAND", nullable = false)
  private String databaseCommand;



  public TemplateEntity(
      Long id, String identifier, String template, String databaseCommand) {
    this.id = id;
    this.identifier = identifier;
    this.template = template;
    this.databaseCommand = databaseCommand;
  }

  public TemplateEntity() {
    super();
  }

  public Long getId() {
    return id;
  }

  @Override
  public void setId(Serializable id) {}

  public void setId(Long id) {
    this.id = id;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getDatabaseCommand() {
    return databaseCommand;
  }

  public void setDatabaseCommand(String databaseCommand) {
    this.databaseCommand = databaseCommand;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}

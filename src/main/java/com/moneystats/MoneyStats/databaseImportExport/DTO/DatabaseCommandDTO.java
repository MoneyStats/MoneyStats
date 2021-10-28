package com.moneystats.MoneyStats.databaseImportExport.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class DatabaseCommandDTO implements Serializable {

  @NotNull private String filePath;
  @NotNull private DatabaseCommand database;
  @NotNull private String role;

  public DatabaseCommandDTO(String filePath, DatabaseCommand database, String role) {
    this.filePath = filePath;
    this.database = database;
    this.role = role;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public DatabaseCommand getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseCommand database) {
    this.database = database;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}

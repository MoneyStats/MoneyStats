package com.moneystats.MoneyStats.databaseImportExport.DTO;

import java.io.Serializable;

public class DatabaseResponseDTO implements Serializable {

  private String response;

  public DatabaseResponseDTO() {
  }

  public DatabaseResponseDTO(String response) {
    this.response = response;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public static enum String {
    EXPORTED,
    IMPORTED
  }
}

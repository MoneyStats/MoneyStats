package com.moneystats.MoneyStats.commStats.statement.DTO;

public class StatementResponseDTO {

  private String message;

  public StatementResponseDTO(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

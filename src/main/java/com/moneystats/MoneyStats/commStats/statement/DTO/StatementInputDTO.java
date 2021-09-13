package com.moneystats.MoneyStats.commStats.statement.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StatementInputDTO implements Serializable {

  @NotNull private Double value;
  @NotNull private String date;
  @NotNull private Long walletId;

  public StatementInputDTO(Double value, String date, Long walletId) {
    this.value = value;
    this.date = date;
    this.walletId = walletId;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Long getWalletId() {
    return walletId;
  }

  public void setWalletId(Long walletId) {
    this.walletId = walletId;
  }
}

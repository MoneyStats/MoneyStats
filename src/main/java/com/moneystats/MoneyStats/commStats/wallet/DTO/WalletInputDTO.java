package com.moneystats.MoneyStats.commStats.wallet.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WalletInputDTO implements Serializable {

  @NotNull private String name;
  @NotNull private Integer categoryId;

  public WalletInputDTO(String name, Integer categoryId) {
    this.name = name;
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }
}

package com.moneystats.MoneyStats.commStats.wallet.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WalletInputIdDTO implements Serializable {

  @NotNull private Long id;
  @NotNull private String name;
  @NotNull private Integer idCategory;

  public WalletInputIdDTO(Long id, String name, Integer idCategory) {
    this.id = id;
    this.name = name;
    this.idCategory = idCategory;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIdCategory() {
    return idCategory;
  }

  public void setIdCategory(Integer idCategory) {
    this.idCategory = idCategory;
  }
}

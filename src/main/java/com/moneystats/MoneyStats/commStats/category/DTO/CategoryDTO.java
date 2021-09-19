package com.moneystats.MoneyStats.commStats.category.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CategoryDTO implements Serializable {

  @NotNull private String name;

  public CategoryDTO(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

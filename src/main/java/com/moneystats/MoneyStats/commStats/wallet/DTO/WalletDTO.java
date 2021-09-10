package com.moneystats.MoneyStats.commStats.wallet.DTO;

import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.authentication.entity.AuthCredentialEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class WalletDTO implements Serializable {

  @NotNull private String name;
  @NotNull private CategoryEntity categoryEntity;
  @NotNull private AuthCredentialEntity user;
  @NotNull private List<StatementEntity> statementEntityList;

  public WalletDTO(
      String name,
      CategoryEntity categoryEntity,
      AuthCredentialEntity user,
      List<StatementEntity> statementEntityList) {
    this.name = name;
    this.categoryEntity = categoryEntity;
    this.user = user;
    this.statementEntityList = statementEntityList;
  }

  public WalletDTO(String name, CategoryEntity categoryEntity, AuthCredentialEntity user) {
    this.name = name;
    this.categoryEntity = categoryEntity;
    this.user = user;
  }

  public WalletDTO() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CategoryEntity getCategoryEntity() {
    return categoryEntity;
  }

  public void setCategoryEntity(CategoryEntity categoryEntity) {
    this.categoryEntity = categoryEntity;
  }

  public AuthCredentialEntity getUser() {
    return user;
  }

  public void setUser(AuthCredentialEntity user) {
    this.user = user;
  }

  public List<StatementEntity> getStatementEntityList() {
    return statementEntityList;
  }

  public void setStatementEntityList(List<StatementEntity> statementEntityList) {
    this.statementEntityList = statementEntityList;
  }
}

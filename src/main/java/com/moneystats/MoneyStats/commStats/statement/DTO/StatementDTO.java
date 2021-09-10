package com.moneystats.MoneyStats.commStats.statement.DTO;

import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.entity.AuthCredentialEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StatementDTO implements Serializable {
  @NotNull private String date;
  @NotNull private Double value;
  @NotNull private AuthCredentialEntity user;
  @NotNull private WalletEntity walletEntity;

  public StatementDTO(
      String date, Double value, AuthCredentialEntity user, WalletEntity walletEntity) {
    this.date = date;
    this.value = value;
    this.user = user;
    this.walletEntity = walletEntity;
  }

  public String getDate() {
    return date;
  }

  public void setDate(@NotNull String date) {
    this.date = date;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(@NotNull Double value) {
    this.value = value;
  }

  public AuthCredentialEntity getUser() {
    return user;
  }

  public void setUser(@NotNull AuthCredentialEntity user) {
    this.user = user;
  }

  public WalletEntity getWalletEntity() {
    return walletEntity;
  }

  public void setWalletEntity(@NotNull WalletEntity walletEntity) {
    this.walletEntity = walletEntity;
  }
}

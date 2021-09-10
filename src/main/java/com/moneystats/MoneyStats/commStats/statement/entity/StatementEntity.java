package com.moneystats.MoneyStats.commStats.statement.entity;

import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.entity.AuthCredentialEntity;

import javax.persistence.*;

@Entity
@Table(name = "statements")
public class StatementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String date;
  private Double value;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private AuthCredentialEntity user;

  @ManyToOne
  @JoinColumn(name = "wallet_id", nullable = false)
  private WalletEntity wallet;

  public StatementEntity(
      Long id, String date, Double value, AuthCredentialEntity user, WalletEntity wallet) {
    this.id = id;
    this.date = date;
    this.value = value;
    this.user = user;
    this.wallet = wallet;
  }

  public StatementEntity(
      String date, Double value, AuthCredentialEntity user, WalletEntity wallet) {
    this.date = date;
    this.value = value;
    this.user = user;
    this.wallet = wallet;
  }

  @Deprecated
  public StatementEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public AuthCredentialEntity getUser() {
    return user;
  }

  public void setUser(AuthCredentialEntity user) {
    this.user = user;
  }

  public WalletEntity getWallet() {
    return wallet;
  }

  public void setWallet(WalletEntity wallet) {
    this.wallet = wallet;
  }
}

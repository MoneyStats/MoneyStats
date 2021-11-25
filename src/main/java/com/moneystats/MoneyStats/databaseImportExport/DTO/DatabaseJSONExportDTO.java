package com.moneystats.MoneyStats.databaseImportExport.DTO;

import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DatabaseJSONExportDTO implements Serializable {

  private Map<String, String> placeholder;
  @NotNull private List<StatementEntity> statementEntities;
  @NotNull private List<WalletEntity> walletEntities;
  @NotNull private List<AuthCredentialEntity> authCredentialEntities;

  public DatabaseJSONExportDTO() {}

  public DatabaseJSONExportDTO(
      List<StatementEntity> statementEntities,
      List<WalletEntity> walletEntities,
      List<AuthCredentialEntity> authCredentialEntities) {
    this.statementEntities = statementEntities;
    this.walletEntities = walletEntities;
    this.authCredentialEntities = authCredentialEntities;
  }

  public DatabaseJSONExportDTO(
      Map<String, String> placeholder,
      List<StatementEntity> statementEntities,
      List<WalletEntity> walletEntities,
      List<AuthCredentialEntity> authCredentialEntities) {
    this.placeholder = placeholder;
    this.statementEntities = statementEntities;
    this.walletEntities = walletEntities;
    this.authCredentialEntities = authCredentialEntities;
  }

  public List<StatementEntity> getStatementEntities() {
    return statementEntities;
  }

  public void setStatementEntities(List<StatementEntity> statementEntities) {
    this.statementEntities = statementEntities;
  }

  public List<WalletEntity> getWalletEntities() {
    return walletEntities;
  }

  public void setWalletEntities(List<WalletEntity> walletEntities) {
    this.walletEntities = walletEntities;
  }

  public List<AuthCredentialEntity> getAuthCredentialEntities() {
    return authCredentialEntities;
  }

  public void setAuthCredentialEntities(List<AuthCredentialEntity> authCredentialEntities) {
    this.authCredentialEntities = authCredentialEntities;
  }

  public Map<String, String> getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(Map<String, String> placeholder) {
    this.placeholder = placeholder;
  }
}

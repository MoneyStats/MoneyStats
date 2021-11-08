package com.moneystats.MoneyStats.databaseImportExport.DTO;

import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.DTO.AuthCredentialDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class DatabaseJSONExportDTO implements Serializable {

  @NotNull private List<StatementEntity> statementEntities;
  @NotNull private List<WalletEntity> walletEntities;
  @NotNull private List<AuthCredentialDTO> authCredentialDTOS;

  public DatabaseJSONExportDTO(
      List<StatementEntity> statementEntities,
      List<WalletEntity> walletEntities,
      List<AuthCredentialDTO> authCredentialDTOS) {
    this.statementEntities = statementEntities;
    this.walletEntities = walletEntities;
    this.authCredentialDTOS = authCredentialDTOS;
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

  public List<AuthCredentialDTO> getAuthCredentialDTOS() {
    return authCredentialDTOS;
  }

  public void setAuthCredentialDTOS(List<AuthCredentialDTO> authCredentialDTOS) {
    this.authCredentialDTOS = authCredentialDTOS;
  }
}

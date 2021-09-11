package com.moneystats.MoneyStats.commStats.wallet.DTO;

import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class WalletStatementDTO implements Serializable {

  @NotNull private List<WalletEntity> walletEntities;
  @NotNull private List<StatementEntity> statementEntities;

  public WalletStatementDTO(
      List<WalletEntity> walletEntities, List<StatementEntity> statementEntities) {
    this.walletEntities = walletEntities;
    this.statementEntities = statementEntities;
  }

  public List<WalletEntity> getWalletEntities() {
    return walletEntities;
  }

  public void setWalletEntities(List<WalletEntity> walletEntities) {
    this.walletEntities = walletEntities;
  }

  public List<StatementEntity> getStatementEntities() {
    return statementEntities;
  }

  public void setStatementEntities(List<StatementEntity> statementEntities) {
    this.statementEntities = statementEntities;
  }
}

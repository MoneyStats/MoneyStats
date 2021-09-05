package com.moneystats.MoneyStats.web.homepage.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class HomepagePieChartDTO implements Serializable {

  @NotNull private List<String> walletList;
  @NotNull private List<Double> statementList;

  public HomepagePieChartDTO(List<String> walletList, List<Double> statementList) {
    this.walletList = walletList;
    this.statementList = statementList;
  }

  public List<String> getWalletList() {
    return walletList;
  }

  public void setWalletList(List<String> walletList) {
    this.walletList = walletList;
  }

  public List<Double> getStatementList() {
    return statementList;
  }

  public void setStatementList(List<Double> statementList) {
    this.statementList = statementList;
  }
}

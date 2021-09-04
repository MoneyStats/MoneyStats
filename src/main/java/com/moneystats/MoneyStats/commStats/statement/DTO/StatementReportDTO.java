package com.moneystats.MoneyStats.commStats.statement.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StatementReportDTO implements Serializable {

  @NotNull private Double statementTotal;
  @NotNull private Double statementTotalPercent;
  @NotNull private Double pilPerformance;
  @NotNull private Double pil;
  @NotNull private Double pilTotal;
  @NotNull private String lastDate;
  @NotNull private String beforeLastDate;
  @NotNull private String firstDate;

  public StatementReportDTO(
      Double statementTotal,
      Double statementTotalPercent,
      Double pilPerformance,
      Double pil,
      Double pilTotal,
      String lastDate,
      String beforeLastDate,
      String firstDate) {
    this.statementTotal = statementTotal;
    this.statementTotalPercent = statementTotalPercent;
    this.pilPerformance = pilPerformance;
    this.pil = pil;
    this.pilTotal = pilTotal;
    this.lastDate = lastDate;
    this.beforeLastDate = beforeLastDate;
    this.firstDate = firstDate;
  }

  public Double getStatementTotalPercent() {
    return statementTotalPercent;
  }

  public void setStatementTotalPercent(Double statementTotalPercent) {
    this.statementTotalPercent = statementTotalPercent;
  }

  public String getLastDate() {
    return lastDate;
  }

  public void setLastDate(String lastDate) {
    this.lastDate = lastDate;
  }

  public String getBeforeLastDate() {
    return beforeLastDate;
  }

  public void setBeforeLastDate(String beforeLastDate) {
    this.beforeLastDate = beforeLastDate;
  }

  public String getFirstDate() {
    return firstDate;
  }

  public void setFirstDate(String firstDate) {
    this.firstDate = firstDate;
  }

  public Double getStatementTotal() {
    return statementTotal;
  }

  public void setStatementTotal(Double statementTotal) {
    this.statementTotal = statementTotal;
  }

  public Double getPilPerformance() {
    return pilPerformance;
  }

  public void setPilPerformance(Double pilPerformance) {
    this.pilPerformance = pilPerformance;
  }

  public Double getPil() {
    return pil;
  }

  public void setPil(Double pil) {
    this.pil = pil;
  }

  public Double getPilTotal() {
    return pilTotal;
  }

  public void setPilTotal(Double pilTotal) {
    this.pilTotal = pilTotal;
  }
}

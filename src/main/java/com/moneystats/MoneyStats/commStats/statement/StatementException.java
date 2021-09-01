package com.moneystats.MoneyStats.commStats.statement;

public class StatementException extends Exception {
  private Code code;

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public StatementException(Code code) {
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public static enum Code {
    INVALID_STATEMENT_DTO,
    USER_NOT_FOUND,
    WALLET_NOT_FOUND,
    STATEMENT_NOT_FOUND
  }
}

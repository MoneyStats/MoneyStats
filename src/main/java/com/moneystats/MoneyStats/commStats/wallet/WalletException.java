package com.moneystats.MoneyStats.commStats.wallet;

public class WalletException extends Exception {

  private Code code;

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public WalletException(Code code) {
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public static enum Code {
    USER_NOT_FOUND,
    WALLET_DTO_NULL,
    CATEGORY_NOT_FOUND,
    INVALID_WALLET_DTO,
    INVALID_WALLET_INPUT_DTO,
    INVALID_WALLET_INPUT_ID_DTO,
    WALLET_NOT_FOUND,
    STATEMENT_NOT_FOUND
  }
}

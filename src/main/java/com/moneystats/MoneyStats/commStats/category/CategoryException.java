package com.moneystats.MoneyStats.commStats.category;

public class CategoryException extends Exception {

  private final Code code;

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public CategoryException(Code code) {
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public static enum Code {
    CATEGORY_NOT_FOUND
  }
}

package com.moneystats.authentication;

public class AuthenticationException extends Exception {

  /** */
  private static final long serialVersionUID = 4370430492546876717L;

  private final Code code;

  public static enum Code {
    DATABASE_ERROR,
    UNAUTHORIZED,
    NOT_ALLOWED,
    INVALID_AUTH_CREDENTIAL_DTO,
    INVALID_AUTH_INPUT_DTO,
    WRONG_CREDENTIAL,
    INVALID_TOKEN_DTO,
    TOKEN_REQUIRED,
    USER_NOT_FOUND,
    USER_PRESENT,
    EMAIL_PRESENT,
    INVALID_AUTH_CREDENTIAL_TO_UPDATE_DTO,
    USER_NOT_MATCH,
    INVALID_AUTH_CHANGE_PASSWORD_INPUT_DTO,
    PASSWORD_NOT_MATCH;
  }

  public AuthenticationException(Code code) {
    super();
    this.code = code;
  }

  public Code getCode() {
    return code;
  }
}

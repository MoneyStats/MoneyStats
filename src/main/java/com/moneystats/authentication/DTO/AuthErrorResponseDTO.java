package com.moneystats.authentication.DTO;

public class AuthErrorResponseDTO {

  private String message;

  public AuthErrorResponseDTO(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

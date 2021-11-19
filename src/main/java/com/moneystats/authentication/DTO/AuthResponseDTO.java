package com.moneystats.authentication.DTO;

import javax.validation.constraints.NotNull;

public class AuthResponseDTO {

  @NotNull private String response;

  public AuthResponseDTO() {}

  public AuthResponseDTO(String response) {
    this.response = response;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public static enum String {
    DELETED,
    USER_ADDED,
    USER_UPDATED,
    PASSWORD_UPDATED
  }
}

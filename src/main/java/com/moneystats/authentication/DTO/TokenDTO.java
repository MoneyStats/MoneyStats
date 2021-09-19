package com.moneystats.authentication.DTO;

import javax.validation.constraints.NotNull;

public class TokenDTO {

  @NotNull private String accessToken;

  public TokenDTO(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}

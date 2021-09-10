package com.moneystats.MoneyStats.web.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WebResponseDTO implements Serializable {

  @NotNull private String message;
  @NotNull private String username;

  public WebResponseDTO(String message, String username) {
    this.message = message;
    this.username = username;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}

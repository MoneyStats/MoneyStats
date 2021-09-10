package com.moneystats.MoneyStats.commStats.wallet.DTO;

import java.io.Serializable;

public class WalletResponseDTO implements Serializable {

  private String message;

  public WalletResponseDTO(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

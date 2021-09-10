package com.moneystats.generic;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

  private String error;

  public ErrorResponse(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}

package com.moneystats.authentication.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class AuthChangePasswordInputDTO implements Serializable {

  @NotNull private String username;
  @NotNull private String oldPassword;
  @NotNull private String newPassword;
  @NotNull private String confirmNewPassword;

  public AuthChangePasswordInputDTO(
      String username, String oldPassword, String newPassword, String confirmNewPassword) {
    this.username = username;
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.confirmNewPassword = confirmNewPassword;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getConfirmNewPassword() {
    return confirmNewPassword;
  }

  public void setConfirmNewPassword(String confirmNewPassword) {
    this.confirmNewPassword = confirmNewPassword;
  }
}

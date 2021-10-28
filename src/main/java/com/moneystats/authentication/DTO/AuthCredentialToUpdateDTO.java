package com.moneystats.authentication.DTO;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class AuthCredentialToUpdateDTO implements Serializable {

  /** */
  private static final long serialVersionUID = 2002768290193136243L;

  @NotNull private String firstName;
  @NotNull private String lastName;
  @NotNull private String dateOfBirth;
  @NotNull private String email;
  @NotNull private String username;

  public AuthCredentialToUpdateDTO(
      @NotNull String firstName,
      @NotNull String lastName,
      @NotNull String dateOfBirth,
      @NotNull String email,
      @NotNull String username) {
    super();
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.email = email;
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}

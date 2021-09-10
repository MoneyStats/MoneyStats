package com.moneystats.authentication.DTO;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class AuthCredentialDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5881782182361395386L;

	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String dateOfBirth;
	@NotNull
	private String email;
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String role;

	public AuthCredentialDTO(String firstName, String lastName, String dateOfBirth, String email, String username,
			String password, String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public AuthCredentialDTO(String firstName, String lastName, String email, String username, String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
		this.role = role;
	}

	public AuthCredentialDTO(String username, String role) {
		this.username = username;
		this.role = role;
	}

  public AuthCredentialDTO(
      String firstName,
      String lastName,
      String dateOfBirth,
      String email,
      String username,
      String role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.username = username;
		this.role = role;
	}

	public AuthCredentialDTO() {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}

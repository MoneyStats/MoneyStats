package com.moneystats.authentication.DTO;

import javax.validation.constraints.NotNull;

public class AuthCredentialInputDTO {

	@NotNull
	private String username;
	@NotNull
	private String password;

	public AuthCredentialInputDTO(String username, String password) {
		this.username = username;
		this.password = password;
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
}

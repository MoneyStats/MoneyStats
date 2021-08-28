package com.moneystats.authentication.DTO;

import javax.validation.constraints.NotNull;

public class AuthResponseDTO {

	@NotNull
	private String message;

	public AuthResponseDTO(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

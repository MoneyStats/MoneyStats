package com.moneystats.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.moneystats.authentication.AuthenticationException.Code;
import com.moneystats.authentication.DTO.AuthErrorResponseDTO;

@ControllerAdvice
public class AuthenticationExceptionMapper {

	@ExceptionHandler(value = AuthenticationException.class)
	public ResponseEntity<AuthErrorResponseDTO> exception(AuthenticationException e) {
		AuthErrorResponseDTO error = new AuthErrorResponseDTO(null);
		e.printStackTrace();
		switch (e.getCode()) {
		case AUTH_CREDENTIAL_DTO_NOT_FOUND:
			error.setMessage(Code.AUTH_CREDENTIAL_DTO_NOT_FOUND.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.NOT_FOUND);
		case DATABASE_ERROR:
			error.setMessage(Code.DATABASE_ERROR.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.NO_CONTENT);
		case INVALID_AUTH_CREDENTIAL_DTO:
			error.setMessage(Code.INVALID_AUTH_CREDENTIAL_DTO.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.BAD_REQUEST);
		case INVALID_AUTH_INPUT_DTO:
			error.setMessage(Code.INVALID_AUTH_INPUT_DTO.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.BAD_REQUEST);
		case INVALID_TOKEN_DTO:
			error.setMessage(Code.INVALID_TOKEN_DTO.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.BAD_REQUEST);
		case NOT_ALLOWED:
			error.setMessage(Code.NOT_ALLOWED.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.UNAUTHORIZED);
		case TOKEN_REQUIRED:
			error.setMessage(Code.TOKEN_REQUIRED.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.BAD_REQUEST);
		case UNAUTHORIZED:
			error.setMessage(Code.UNAUTHORIZED.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.UNAUTHORIZED);
		case WRONG_CREDENTIAL:
			error.setMessage(Code.WRONG_CREDENTIAL.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.UNAUTHORIZED);
		case USER_PRESENT:
			error.setMessage(Code.USER_PRESENT.toString());
			return new ResponseEntity<AuthErrorResponseDTO>(error, HttpStatus.BAD_REQUEST);
		default:
			error.setMessage("INTERNAL_SERVER_ERROR");
			return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

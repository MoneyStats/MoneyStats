package com.moneystats.authentication;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;

public class AuthenticationValidator {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationValidator.class);

	public static void validateAuthCredentialDTO(AuthCredentialDTO authCredentialDTO) throws AuthenticationException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<AuthCredentialDTO>> violations = validator.validate(authCredentialDTO);
		if (!violations.isEmpty()) {
			LOG.warn("Invalid Auth DTO {}", authCredentialDTO);
			throw new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO);
		}
	}

	public static void validateAuthCredentialInputDTO(AuthCredentialInputDTO authCredentialInputDTO)
			throws AuthenticationException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<AuthCredentialInputDTO>> violations = validator.validate(authCredentialInputDTO);
		if (!violations.isEmpty()) {
			LOG.warn("Invalid Auth Input {}", authCredentialInputDTO);
			throw new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_INPUT_DTO);
		}
	}
}

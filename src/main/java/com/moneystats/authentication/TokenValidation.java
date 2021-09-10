package com.moneystats.authentication;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moneystats.authentication.DTO.TokenDTO;

public class TokenValidation {
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationValidator.class);

  public static void validateTokenDTO(TokenDTO tokenDTO) throws AuthenticationException {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<TokenDTO>> violations = validator.validate(tokenDTO);
    if (!violations.isEmpty()) {
      LOG.warn("Invalid Token DTO {}", tokenDTO);
      throw new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    }
  }
}

package com.moneystats.authentication;

import com.moneystats.authentication.DTO.TokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

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

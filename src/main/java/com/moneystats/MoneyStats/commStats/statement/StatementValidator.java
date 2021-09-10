package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class StatementValidator {
  private static final Logger LOG = LoggerFactory.getLogger(StatementValidator.class);

  public static void validateStatementDTO(StatementDTO statementDTO) throws StatementException {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<StatementDTO>> violations = validator.validate(statementDTO);

    if (violations.size() > 0) {
      LOG.warn("Invalid Statement {}", statementDTO);
      throw new StatementException(StatementException.Code.INVALID_STATEMENT_DTO);
    }
  }
}

package com.moneystats.MoneyStats.databaseImportExport;

import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.StatementValidator;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class DatabaseValidator {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseValidator.class);

    @LoggerMethod(type = LogTimeTracker.ActionType.APP_VALIDATOR)
    public static void validateDatabaseCommandDTO(DatabaseCommandDTO databaseCommandDTO) throws DatabaseException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<DatabaseCommandDTO>> violations = validator.validate(databaseCommandDTO);

        if (violations.size() > 0) {
            LOG.warn("Invalid DatabaseDTO {}", databaseCommandDTO);
            throw new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);
        }
        if (!databaseCommandDTO.getRole().equalsIgnoreCase(SecurityRoles.MONEYSTATS_ADMIN_ROLE)){
            LOG.error("The current user is not an ADMIN user, SecurityRoles: {}", databaseCommandDTO.getRole());
            throw new DatabaseException(DatabaseException.Code.NOT_ADMIN_USER);
        }
    }
}

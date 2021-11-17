package com.moneystats.MoneyStats.databaseExportTest;

import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseException;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseValidator;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.authentication.SecurityRoles;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseValidatorTest {

  @ParameterizedTest
  @MethodSource("providesDifferentValue")
  public void testValidateDatabaseCommandDTO(
      DatabaseCommandDTO databaseCommandDTO, DatabaseException.Code exceptionExpected) {
    DatabaseException exception =
        assertThrows(
            DatabaseException.class,
            () -> DatabaseValidator.validateDatabaseCommandDTO(databaseCommandDTO));

    assertEquals(exceptionExpected, exception.getCode());
  }

  public static Stream<Arguments> providesDifferentValue() {
    DatabaseCommandDTO invalidDatabaseCommand =
        new DatabaseCommandDTO(
            TemplatePlaceholders.FILEPATH_BACKUP, null, SecurityRoles.MONEYSTATS_ADMIN_ROLE);
    DatabaseCommandDTO invalidRole =
        new DatabaseCommandDTO(
            TemplatePlaceholders.FILEPATH_BACKUP, DatabaseCommand.EXPORT_DUMP_COMMAND, null);
    DatabaseCommandDTO userRole =
        new DatabaseCommandDTO(
            TemplatePlaceholders.FILEPATH_BACKUP,
            DatabaseCommand.EXPORT_DUMP_COMMAND,
            SecurityRoles.MONEYSTATS_USER_ROLE);
    return Stream.of(
        Arguments.of(invalidDatabaseCommand, DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO),
        Arguments.of(invalidRole, DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO),
        Arguments.of(userRole, DatabaseException.Code.NOT_ADMIN_USER));
  }
}

package com.moneystats.MoneyStats.databaseImportExport;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DatabaseExceptionMapper {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(value = DatabaseException.class)
    public ResponseEntity<ErrorResponse> exception(DatabaseException e) {
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
        e.printStackTrace();
        switch (e.getCode()) {
            case INVALID_DATABASE_COMMAND_DTO:
                error.setError(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO.toString());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            case ERROR_ON_IMPORT_DATABASE:
                error.setError(DatabaseException.Code.ERROR_ON_IMPORT_DATABASE.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case ERROR_ON_EXPORT_DATABASE:
                error.setError(DatabaseException.Code.ERROR_ON_EXPORT_DATABASE.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case NOT_ADMIN_USER:
                error.setError(DatabaseException.Code.NOT_ADMIN_USER.toString());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            case FILE_NOT_FOUND:
                error.setError(DatabaseException.Code.FILE_NOT_FOUND.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case CONNECTION_FAILED:
                error.setError(DatabaseException.Code.CONNECTION_FAILED.toString());
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            default:
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

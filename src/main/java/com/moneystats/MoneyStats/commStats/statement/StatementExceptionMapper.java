package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StatementExceptionMapper {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(value = StatementException.class)
    public ResponseEntity<ErrorResponse> exception(StatementException e) {
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
        e.printStackTrace();
        switch (e.getCode()) {
            case USER_NOT_FOUND:
                error.setError(StatementException.Code.USER_NOT_FOUND.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case INVALID_STATEMENT_DTO:
                error.setError(StatementException.Code.INVALID_STATEMENT_DTO.toString());
                return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
            case WALLET_NOT_FOUND:
                error.setError(StatementException.Code.WALLET_NOT_FOUND.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            case STATEMENT_NOT_FOUND:
                error.setError(StatementException.Code.STATEMENT_NOT_FOUND.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

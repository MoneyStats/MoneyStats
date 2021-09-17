package com.moneystats.MoneyStats.web;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionMapper {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(value = WebException.class)
    public ResponseEntity<ErrorResponse> exception(WebException e) {
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
        e.printStackTrace();
        switch (e.getCode()) {
            case LOGIN_REQUIRED:
                error.setError(WebException.Code.LOGIN_REQUIRED.toString());
                return new ResponseEntity<>(error, HttpStatus.REQUEST_TIMEOUT);
            default:
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

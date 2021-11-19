package com.moneystats.MoneyStats.databaseImportExport.template;

import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TemplateExceptionMapper {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(value = TemplateException.class)
    public ResponseEntity<ErrorResponse> exception(TemplateException e) {
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
        e.printStackTrace();
        switch (e.getCode()) {
            case IMPOSSIBLE_TO_WRITE_THE_TEMPLATE:
                error.setError(TemplateException.Code.IMPOSSIBLE_TO_WRITE_THE_TEMPLATE.toString());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            case TEMPLATE_NOT_FOUND:
                error.setError(TemplateException.Code.TEMPLATE_NOT_FOUND.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

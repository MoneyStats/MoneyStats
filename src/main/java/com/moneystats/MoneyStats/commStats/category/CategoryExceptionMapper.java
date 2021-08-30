package com.moneystats.MoneyStats.commStats.category;

import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthErrorResponseDTO;
import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CategoryExceptionMapper {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(value = CategoryException.class)
    public ResponseEntity<ErrorResponse> exception(CategoryException e) {
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
        e.printStackTrace();
        switch (e.getCode()) {
            case CATEGORY_NOT_FOUND:
                error.setError(CategoryException.Code.CATEGORY_NOT_FOUND.toString());
                return new ResponseEntity<ErrorResponse>(error, HttpStatus.NO_CONTENT);
            default:
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

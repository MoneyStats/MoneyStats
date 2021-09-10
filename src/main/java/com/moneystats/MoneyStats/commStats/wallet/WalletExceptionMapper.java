package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.generic.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WalletExceptionMapper {
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

  @ExceptionHandler(value = WalletException.class)
  public ResponseEntity<ErrorResponse> exception(WalletException e) {
    ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR);
    e.printStackTrace();
    switch (e.getCode()) {
      case USER_NOT_FOUND:
        error.setError(WalletException.Code.USER_NOT_FOUND.toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
      case WALLET_DTO_NULL:
        error.setError(WalletException.Code.WALLET_DTO_NULL.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
      case CATEGORY_NOT_FOUND:
        error.setError(WalletException.Code.CATEGORY_NOT_FOUND.toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
      case INVALID_WALLET_DTO:
        error.setError(WalletException.Code.INVALID_WALLET_DTO.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      case WALLET_NOT_FOUND:
        error.setError(WalletException.Code.WALLET_NOT_FOUND.toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
      case STATEMENT_NOT_FOUND:
        error.setError(WalletException.Code.STATEMENT_NOT_FOUND.toString());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
      default:
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

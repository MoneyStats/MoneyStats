package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class WalletValidator {
  private static final Logger LOG = LoggerFactory.getLogger(WalletValidator.class);

  public static void validateWalletDTO(WalletDTO walletDTO) throws WalletException {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<WalletDTO>> violationSet = validator.validate(walletDTO);

    if (violationSet.size() > 0) {
      LOG.warn("Invalid Statement {}", walletDTO);
      throw new WalletException(WalletException.Code.INVALID_WALLET_DTO);
    }
  }
}

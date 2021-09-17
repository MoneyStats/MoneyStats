package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputIdDTO;
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

    if (!violationSet.isEmpty()) {
      LOG.warn("Invalid Wallet {}", walletDTO);
      throw new WalletException(WalletException.Code.INVALID_WALLET_DTO);
    }
  }

  public static void validateWalletInputDTO(WalletInputDTO walletDTO) throws WalletException {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<WalletInputDTO>> violationSet = validator.validate(walletDTO);

    if (!violationSet.isEmpty()) {
      LOG.warn("Invalid Wallet {}", walletDTO);
      throw new WalletException(WalletException.Code.INVALID_WALLET_INPUT_DTO);
    }
  }
  public static void validateWalletInputWithIDDTO(WalletInputIdDTO walletDTO) throws WalletException {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<WalletInputIdDTO>> violationSet = validator.validate(walletDTO);

    if (!violationSet.isEmpty()) {
      LOG.warn("Invalid Wallet {}", walletDTO);
      throw new WalletException(WalletException.Code.INVALID_WALLET_INPUT_ID_DTO);
    }
  }
}

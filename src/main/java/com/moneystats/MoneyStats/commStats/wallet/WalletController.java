package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletStatementDTO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

  @Autowired private WalletService walletService;

  /**
   * @param jwt token for authentications
   * @return a list of wallet
   * @throws WalletException
   * @throws AuthenticationException
   */
  @GetMapping("/list")
  public List<WalletEntity> getAll(@RequestHeader(value = "Authorization") String jwt)
      throws WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.getAll(tokenDTO);
  }

  /**
   * @param jwt token for authentications
   * @param idCategory category to be linked
   * @param walletDTO params
   * @return response od success or not
   * @throws WalletException
   * @throws AuthenticationException
   */
  @PostMapping("/addWallet/{idCategory}")
  public WalletResponseDTO addWallet(
      @RequestHeader(value = "Authorization") String jwt,
      @PathVariable int idCategory,
      @RequestBody WalletInputDTO walletDTO)
      throws WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.addWalletEntity(tokenDTO, idCategory, walletDTO);
  }

  /**
   * @param idWallet to be deleted
   * @return response of success or error
   * @throws WalletException
   */
  @DeleteMapping("/delete/{idWallet}")
  public WalletResponseDTO deleteWallet(@PathVariable long idWallet) throws WalletException {
    return walletService.deleteWalletEntity(idWallet);
  }

  @GetMapping("/listMobile")
  public WalletStatementDTO walletListMobile(@RequestHeader(value = "Authorization") String jwt)
      throws WalletException, StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.myWalletMobile(tokenDTO);
  }
}

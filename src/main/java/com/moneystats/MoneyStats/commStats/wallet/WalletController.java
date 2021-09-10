package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

  @Autowired private WalletService walletService;

  @GetMapping("/list")
  public List<WalletDTO> getAll(@RequestHeader(value = "Authorization") String jwt)
      throws WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.getAll(tokenDTO);
  }

  @PostMapping("/addWallet/{idCategory}")
  public WalletResponseDTO addWallet(
      @RequestHeader(value = "Authorization") String jwt,
      @PathVariable int idCategory,
      @RequestBody WalletDTO walletDTO)
      throws WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.addWalletEntity(tokenDTO, idCategory, walletDTO);
  }

  @DeleteMapping("/delete/{idWallet}")
  public WalletResponseDTO deleteWallet(@PathVariable long idWallet) throws WalletException {
    return walletService.deleteWalletEntity(idWallet);
  }
}

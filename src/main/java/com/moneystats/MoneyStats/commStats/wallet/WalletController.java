package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.category.CategoryException;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.DTO.*;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@OpenAPIDefinition(tags = {@Tag(name = "Wallet", description = "")})
public class WalletController {

  @Autowired private WalletService walletService;

  /**
   * @param jwt token for authentications
   * @return a list of wallet
   * @throws WalletException
   * @throws AuthenticationException
   */
  @GetMapping("/list")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_ALL_WALLET_SUMMARY,
      description = SchemaDescription.GET_ALL_WALLET_DESCRIPTION,
      tags = "Wallet")
  public List<WalletEntity> getAll(@RequestHeader(value = "Authorization") String jwt)
      throws WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.getAll(tokenDTO);
  }

  /**
   * @param jwt token for authentications
   * @param walletDTO params
   * @return response od success or not
   * @throws WalletException
   * @throws AuthenticationException
   */
  @PostMapping("/addWallet")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.POST_ADD_WALLET_SUMMARY,
      description = SchemaDescription.POST_ADD_WALLET_DESCRIPTION,
      tags = "Wallet")
  public WalletResponseDTO addWallet(
      @RequestHeader(value = "Authorization") String jwt, @RequestBody WalletInputDTO walletDTO)
      throws WalletException, AuthenticationException, CategoryException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.addWalletEntity(tokenDTO, walletDTO);
  }

  /**
   * @param idWallet to be deleted
   * @return response of success or error
   * @throws WalletException
   */
  @DeleteMapping("/delete/{idWallet}")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.DELETE_WALLET_SUMMARY,
      description = SchemaDescription.DELETE_WALLET_DESCRIPTION,
      tags = "Wallet")
  public WalletResponseDTO deleteWallet(@PathVariable long idWallet) throws WalletException {
    return walletService.deleteWalletEntity(idWallet);
  }

  /**
   * List used on mobile device
   *
   * @param jwt for auth
   * @return List of wallet and List of Statement
   * @throws WalletException
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/listMobile")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_WALLET_STATEMENT_SUMMARY,
      description = SchemaDescription.GET_WALLET_STATEMENT_DESCRIPTION,
      tags = "Wallet")
  public WalletStatementDTO walletListMobile(@RequestHeader(value = "Authorization") String jwt)
      throws WalletException, StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.myWalletMobile(tokenDTO);
  }

  /**
   * Wallet To Be Edited
   *
   * @param jwt for auth
   * @param walletInputIdDTO wallet in input
   * @return A response of status
   * @throws WalletException
   * @throws AuthenticationException
   * @throws CategoryException
   */
  @PutMapping("/editWallet")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.PUT_UPDATE_WALLET_SUMMARY,
      description = SchemaDescription.PUT_UPDATE_WALLET_DESCRIPTION,
      tags = "Wallet")
  public WalletResponseDTO editWallet(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody WalletInputIdDTO walletInputIdDTO)
      throws WalletException, AuthenticationException, CategoryException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return walletService.editWallet(walletInputIdDTO, tokenDTO);
  }

  /**
   * Get single wallet by id
   *
   * @param idWallet
   * @return WalletDTO
   * @throws WalletException
   */
  @GetMapping("/getById/{idWallet}")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_WALLET_BY_ID_SUMMARY,
      description = SchemaDescription.GET_WALLET_BY_ID_DESCRIPTION,
      tags = "Wallet")
  public WalletDTO getById(@PathVariable long idWallet) throws WalletException {
    return walletService.walletById(idWallet);
  }
}

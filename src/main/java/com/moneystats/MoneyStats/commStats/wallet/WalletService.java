package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletStatementDTO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.TokenValidation;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.SchemaDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Autowired private TokenService tokenService;
  @Autowired private IWalletDAO walletDAO;
  @Autowired private ICategoryDAO categoryDAO;
  @Autowired private IStatementDAO statementDAO;
  @Autowired private AuthCredentialDAO authCredentialDAO;

  /**
   * Used to get all the wallet from db
   *
   * @param tokenDTO
   * @return
   * @throws WalletException
   * @throws AuthenticationException
   */
  public List<WalletEntity> getAll(TokenDTO tokenDTO)
      throws WalletException, AuthenticationException {
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    List<WalletEntity> walletEntities = walletDAO.findAllByUserId(utente.getId());
    if (walletEntities.size() == 0) {
      LOG.error("Wallet Not Found on getAll, WalletService:41");
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    /*List<WalletEntity> walletDTOS = new ArrayList<>();
    WalletDTO walletDTO = null;
    for (int i = 0; i < walletEntities.size(); i++) {
      walletDTO =
          new WalletDTO(
              walletEntities.get(i).getName(),
              walletEntities.get(i).getCategory(),
              walletEntities.get(i).getUser(),
              walletEntities.get(i).getStatementList());
      walletDTOS.add(walletDTO);
    }*/
    return walletEntities;
  }

  /**
   * Used in the controller to add the wallet into the database
   *
   * @param tokenDTO for auth
   * @param idCategory to link
   * @param walletDTO to be added
   * @return a response of status
   * @throws WalletException
   * @throws AuthenticationException
   */
  public WalletResponseDTO addWalletEntity(
      TokenDTO tokenDTO, Integer idCategory, WalletDTO walletDTO)
      throws WalletException, AuthenticationException {
    WalletValidator.validateWalletDTO(walletDTO);
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    walletDTO.setUser(utente);
    CategoryEntity category = categoryDAO.findById(idCategory).orElse(null);
    if (category == null) {
      LOG.error("Category Not Found, on addWalletEntity into WalletService:67");
      throw new WalletException(WalletException.Code.CATEGORY_NOT_FOUND);
    }
    walletDTO.setCategoryEntity(category);
    WalletEntity walletEntity =
        new WalletEntity(
            walletDTO.getName(),
            walletDTO.getCategoryEntity(),
            walletDTO.getUser(),
            walletDTO.getStatementEntityList());
    walletDAO.save(walletEntity);
    return new WalletResponseDTO(SchemaDescription.WALLET_ADDED_CORRECT);
  }

  /**
   * Used to delete a wallet from the database
   *
   * @param idWallet to be deleted
   * @return response of status
   * @throws WalletException
   */
  public WalletResponseDTO deleteWalletEntity(Long idWallet) throws WalletException {
    WalletEntity wallet = walletDAO.findById(idWallet).orElse(null);
    if (wallet == null) {
      LOG.error("Wallet Not Found, on deleteWalletEntity into WalletService:84");
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    List<StatementEntity> statementEntities = statementDAO.findStatementByWalletId(wallet.getId());
    if (statementEntities.size() == 0) {
      LOG.error("No Statement Found, on deleteWalletEntity into WalletService:90");
      throw new WalletException(WalletException.Code.STATEMENT_NOT_FOUND);
    }
    wallet.setStatementList(statementEntities);
    for (StatementEntity statementEntity : wallet.getStatementList()) {
      statementDAO.deleteById(statementEntity.getId());
    }
    walletDAO.deleteById(idWallet);
    return new WalletResponseDTO(SchemaDescription.WALLET_DELETE_CORRECT);
  }

  public WalletStatementDTO myWalletMobile(TokenDTO tokenDTO)
      throws AuthenticationException, StatementException, WalletException {
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    WalletStatementDTO walletStatementDTO = new WalletStatementDTO(null, null);

    List<String> listDate = statementDAO.selectdistinctstatement(utente.getId());
    if (listDate.size() == 0) {
      LOG.error(
          "Statement Date Not Found, into WalletService, statementDAO.selectdistinctstatement(utente.getId()):135");
      throw new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND);
    }
    String date = listDate.get(listDate.size() - 1);
    LOG.info("Date my wallet mobile {}", date);

    List<WalletEntity> walletEntities = walletDAO.findAllByUserId(utente.getId());
    if (walletEntities.size() == 0) {
      LOG.error("Wallet Not Found on getAll, WalletService:41");
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    List<StatementEntity> statementList =
        statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date);
    if (statementList.size() == 0) {
      LOG.error(
          "Statement Not Found, into WalletService, statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date):150");
      throw new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
    }
    walletStatementDTO.setWalletEntities(walletEntities);
    walletStatementDTO.setStatementEntities(statementList);
    return walletStatementDTO;
  }

  /**
   * Method in common on getAll and addWallet
   *
   * @param tokenDTO used for validations
   * @return an user
   * @throws AuthenticationException
   */
  private AuthCredentialEntity validateAndCreate(TokenDTO tokenDTO) throws AuthenticationException {
    TokenValidation.validateTokenDTO(tokenDTO);
    if (tokenDTO.getAccessToken().equalsIgnoreCase("")) {
      throw new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    }
    AuthCredentialDTO authCredentialDTO = tokenService.parseToken(tokenDTO);
    AuthCredentialInputDTO authCredentialInputDTO =
        new AuthCredentialInputDTO(
            authCredentialDTO.getUsername(), authCredentialDTO.getPassword());
    return authCredentialDAO.getCredential(authCredentialInputDTO);
  }
}

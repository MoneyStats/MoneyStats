package com.moneystats.MoneyStats.commStats.wallet;

import com.moneystats.MoneyStats.commStats.category.CategoryException;
import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.*;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.TokenValidation;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.ResponseMapping;
import com.moneystats.timeTracker.LogTimeTracker;
import com.moneystats.timeTracker.LoggerMethod;
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
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public List<WalletEntity> getAll(TokenDTO tokenDTO)
      throws WalletException, AuthenticationException {
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    List<WalletEntity> walletEntities = walletDAO.findAllByUserId(utente.getId());
    if (walletEntities.size() == 0) {
      LOG.error(
          "Wallet Not Found on getAll, WalletService:54, Exception {}",
          WalletException.Code.WALLET_NOT_FOUND.toString());
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    return walletEntities;
  }

  /**
   * Used in the controller to add the wallet into the database
   *
   * @param tokenDTO for auth
   * @param walletInputDTO to be added
   * @return a response of status
   * @throws WalletException
   * @throws AuthenticationException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public WalletResponseDTO addWalletEntity(TokenDTO tokenDTO, WalletInputDTO walletInputDTO)
      throws WalletException, AuthenticationException, CategoryException {
    WalletValidator.validateWalletInputDTO(walletInputDTO);
    WalletDTO walletDTO = new WalletDTO();
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    walletDTO.setUser(utente);
    CategoryEntity category = categoryDAO.findById(walletInputDTO.getCategoryId()).orElse(null);
    if (category == null) {
      LOG.error(
          "Category Not Found, on addWalletEntity into WalletService:67, Exception {}",
          CategoryException.Code.CATEGORY_NOT_FOUND.toString());
      throw new CategoryException(CategoryException.Code.CATEGORY_NOT_FOUND);
    }
    walletDTO.setName(walletInputDTO.getName());
    walletDTO.setCategoryEntity(category);
    WalletEntity walletEntity =
        new WalletEntity(
            walletDTO.getName(),
            walletDTO.getCategoryEntity(),
            walletDTO.getUser(),
            walletDTO.getStatementEntityList());
    walletDAO.save(walletEntity);
    return new WalletResponseDTO(ResponseMapping.WALLET_ADDED_CORRECT);
  }

  /**
   * Used to delete a wallet from the database
   *
   * @param idWallet to be deleted
   * @return response of status
   * @throws WalletException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public WalletResponseDTO deleteWalletEntity(Long idWallet) throws WalletException {
    WalletEntity wallet = walletDAO.findById(idWallet).orElse(null);
    if (wallet == null) {
      LOG.error(
          "Wallet Not Found, on deleteWalletEntity into WalletService:108, Exception {}",
          WalletException.Code.WALLET_NOT_FOUND.toString());
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    List<StatementEntity> statementEntities = statementDAO.findStatementByWalletId(wallet.getId());
    if (statementEntities.size() == 0) {
      LOG.error("No Statement Found, on deleteWalletEntity into WalletService:115, No Exception Throws");
    }
    wallet.setStatementList(statementEntities);
    for (StatementEntity statementEntity : wallet.getStatementList()) {
      statementDAO.deleteById(statementEntity.getId());
    }
    walletDAO.deleteById(idWallet);
    return new WalletResponseDTO(ResponseMapping.WALLET_DELETE_CORRECT);
  }

  /**
   * Methods to get a list of wallet and list of statement
   *
   * @param tokenDTO
   * @return List of statement and list of wallet
   * @throws AuthenticationException
   * @throws StatementException
   * @throws WalletException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public WalletStatementDTO myWalletMobile(TokenDTO tokenDTO)
      throws AuthenticationException, StatementException, WalletException {
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    WalletStatementDTO walletStatementDTO = new WalletStatementDTO(null, null);

    List<String> listDate = statementDAO.selectdistinctstatement(utente.getId());
    if (listDate.size() == 0) {
      LOG.error(
          "List Date Not Found, into WalletService, myWalletMobile:143, Exception {}",
          StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND.toString());
      throw new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND);
    }
    String date = listDate.get(listDate.size() - 1);

    List<WalletEntity> walletEntities = walletDAO.findAllByUserId(utente.getId());
    if (walletEntities.size() == 0) {
      LOG.error(
          "Wallet Not Found on getAll, WalletService:151, Exception {}",
          WalletException.Code.WALLET_NOT_FOUND.toString());
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    List<StatementEntity> statementList =
        statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date);
    if (statementList.size() == 0) {
      LOG.error(
          "Statement Not Found, into WalletService, List statement entity ordered by walletID:160, Exception {}",
          StatementException.Code.STATEMENT_NOT_FOUND.toString());
      throw new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
    }

    // Fix addWallet dont show wallet
    if (walletEntities.size() > statementList.size()) {
      int walletPlus = walletEntities.size() - statementList.size();
      for (int i = 0; i < walletPlus; i++) {
        StatementEntity statementEntity =
            new StatementEntity(date, 0.00D, utente, walletEntities.get(i));
        statementList.add(statementEntity);
      }
    }
    walletStatementDTO.setWalletEntities(walletEntities);
    walletStatementDTO.setStatementEntities(statementList);
    return walletStatementDTO;
  }

  /**
   * Edit Wallet Methods, check on user, check category, and then update db
   *
   * @param walletInputIdDTO to be edited
   * @param token for auth
   * @return response od status
   * @throws WalletException
   * @throws AuthenticationException
   * @throws CategoryException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public WalletResponseDTO editWallet(WalletInputIdDTO walletInputIdDTO, TokenDTO token)
      throws WalletException, AuthenticationException, CategoryException {
    WalletValidator.validateWalletInputWithIDDTO(walletInputIdDTO);
    AuthCredentialEntity utente = validateAndCreate(token);
    CategoryEntity categoryEntity =
        categoryDAO.findById(walletInputIdDTO.getIdCategory()).orElse(null);
    if (categoryEntity == null) {
      LOG.error(
          "Category Not Found, on editWallet into WalletService:197, Exception {}",
          CategoryException.Code.CATEGORY_NOT_FOUND.toString());
      throw new CategoryException(CategoryException.Code.CATEGORY_NOT_FOUND);
    }
    WalletEntity walletEntityToEdit =
        new WalletEntity(
            walletInputIdDTO.getId(), walletInputIdDTO.getName(), categoryEntity, utente, null);
    walletDAO.save(walletEntityToEdit);
    return new WalletResponseDTO(ResponseMapping.WALLET_EDIT_CORRECT);
  }

  /**
   * Get single wallet
   *
   * @param idWallet to be get
   * @return WalletDTO
   * @throws WalletException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
  public WalletDTO walletById(Long idWallet) throws WalletException {
    WalletEntity walletEntity = walletDAO.findById(idWallet).orElse(null);
    if (walletEntity == null) {
      LOG.error(
          "WalletEntity Not Found, on walletById into WalletService:220, Exception {}",
          WalletException.Code.WALLET_NOT_FOUND.toString());
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    WalletDTO walletDTO =
        new WalletDTO(
            walletEntity.getName(),
            walletEntity.getCategory(),
            walletEntity.getUser(),
            walletEntity.getStatementList());
    return walletDTO;
  }

  /**
   * Method in common on getAll and addWallet
   *
   * @param tokenDTO used for validations
   * @return an user
   * @throws AuthenticationException
   */
  @LoggerMethod(type = LogTimeTracker.ActionType.APP_SERVICE_LOGIC)
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

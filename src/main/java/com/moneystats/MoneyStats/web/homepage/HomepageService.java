package com.moneystats.MoneyStats.web.homepage;

import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.TokenValidation;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomepageService {

  @Autowired private IWalletDAO walletDAO;
  @Autowired private IStatementDAO statementDAO;
  @Autowired private AuthCredentialDAO authCredentialDAO;
  @Autowired private TokenService tokenService;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  /**
   * @param tokenDTO to access at the user
   * @return a report to be access to the homapage
   * @throws StatementException
   * @throws AuthenticationException
   */
  public HomepageReportDTO reportHomepage(TokenDTO tokenDTO)
      throws StatementException, AuthenticationException {
    HomepageReportDTO reportDTO =
        new HomepageReportDTO(0.00D, 0.00D, 0.00D, 0.00D, 0.00, "", "", "", null, null, null);
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);

    List<String> listDate = statementDAO.selectdistinctstatement(utente.getId());
    if (listDate.size() == 0) {
      LOG.error(
          "Statement Date Not Found, into StatementService, statementDAO.selectdistinctstatement(utente.getId()):61");
      throw new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND);
    }
    String lastDate = null;
    String firstDate = null;
    String lastStatementDate = null;
    List<Double> listStatement = new ArrayList<>();
    for (int i = 0; i < listDate.size(); i++) {
      firstDate = listDate.get(0);
      lastDate = listDate.get(listDate.size() - 1);
      lastStatementDate = listDate.get(listDate.size() - 2);
    }
    Double statementReport = 0D;
    for (int i = 0; i < listDate.size(); i++) {
      List<StatementEntity> listStatementBylastDate =
          statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), listDate.get(i));
      statementReport = 0D;
      for (int y = 0; y < listStatementBylastDate.size(); y++) {
        statementReport += listStatementBylastDate.get(y).getValue();
      }
      listStatement.add(statementReport);
    }
    LOG.info("List Statement {}", listStatement);
    List<Double> listPil = new ArrayList<>();
    listPil.add(0.00D);
    for (int i = 1; i < listStatement.size(); i++) {
      Double pil = listStatement.get(i) - listStatement.get(i - 1);
      listPil.add(pil);
    }
    LOG.info("List PIL {}", listPil);
    List<StatementEntity> listStatementByPreviousDate =
        listStatementReportCalc(utente, lastStatementDate);

    Double previousStatementReport = 0D;
    for (int i = 0; i < listStatementByPreviousDate.size(); i++) {
      previousStatementReport += listStatementByPreviousDate.get(i).getValue();
    }

    List<StatementEntity> listStatementByFirstDate = listStatementReportCalc(utente, firstDate);

    Double firstStatementReport = 0D;
    for (int i = 0; i < listStatementByFirstDate.size(); i++) {
      firstStatementReport += listStatementByFirstDate.get(i).getValue();
    }

    Double pilPerformance = ((statementReport - firstStatementReport) / firstStatementReport) * 100;
    Double statementPercent =
        ((statementReport - previousStatementReport) / previousStatementReport) * 100;

    reportDTO.setStatementTotal(statementReport);
    reportDTO.setStatementTotalPercent(statementPercent);
    reportDTO.setPil(statementReport - previousStatementReport);
    reportDTO.setPilPerformance(pilPerformance);
    reportDTO.setPilTotal(statementReport - firstStatementReport);
    reportDTO.setLastDate(lastDate);
    reportDTO.setFirstDate(firstDate);
    reportDTO.setBeforeLastDate(lastStatementDate);
    reportDTO.setListDate(listDate);
    reportDTO.setStatementList(listStatement);
    reportDTO.setListPil(listPil);
    return reportDTO;
  }

  /**
   * Get the Pie Graph on the Homepage
   * @param date required to get all the statement by that date
   * @param tokenDTO required to authenticate user
   * @return An ojbect with statement and wallet list
   * @throws StatementException
   * @throws AuthenticationException
   * @throws WalletException
   */
  public HomepagePieChartDTO getGraph(String date, TokenDTO tokenDTO)
      throws StatementException, AuthenticationException, WalletException {
    AuthCredentialEntity utente = validateAndCreate(tokenDTO);
    HomepagePieChartDTO homepagePieChartDTO = new HomepagePieChartDTO(null, null);
    List<StatementEntity> statementList =
        statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date);
    if (statementList.size() == 0) {
      LOG.error(
          "Statement Not Found, into StatementService, statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date):71");
      throw new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
    }
    List<WalletEntity> walletEntities = walletDAO.findAllByUserId(utente.getId());
    if (walletEntities.size() == 0) {
      LOG.error("Wallet Not Found on getAll, WalletService:41");
      throw new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    }
    List<String> walletListGraph = new ArrayList<>();
    List<Double> statementListGraph = new ArrayList<>();

    // Fix addWallet with no statement
    if (walletEntities.size() > statementList.size()){
      int walletPlus = walletEntities.size() - statementList.size();
      for (int i = 0; i < walletPlus; i ++){
        StatementEntity statementEntity = new StatementEntity(date, 0.00D, utente, walletEntities.get(i));
        statementList.add(statementEntity);
      }
    }

    for (int i = 0; i < walletEntities.size(); i++) {
      walletListGraph.add(walletEntities.get(i).getName());
      statementListGraph.add(statementList.get(i).getValue());
    }
    homepagePieChartDTO.setWalletList(walletListGraph);
    homepagePieChartDTO.setStatementList(statementListGraph);
    LOG.info("List of Wallet on HomepageService:138 {}", homepagePieChartDTO.getWalletList());
    LOG.info("List of Statement on HomepageService:139 {}", homepagePieChartDTO.getStatementList());
    return homepagePieChartDTO;
  }

  /**
   * Used on getHomepage
   * @param utente
   * @param date
   * @return
   * @throws StatementException
   */
  private List<StatementEntity> listStatementReportCalc(AuthCredentialEntity utente, String date)
      throws StatementException {
    List<StatementEntity> list =
        statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date);
    if (list.size() == 0) {
      LOG.error(
          "Statement Not Found, into StatementService, statementDAO.findAllByUserIdAndDateOrderByWalletId(utente.getId(), date):71");
      throw new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
    }
    return list;
  }

  /**
   * Method in common
   * @param tokenDTO required for authentications
   * @return An user
   * @throws AuthenticationException
   * @throws StatementException
   */
  private AuthCredentialEntity validateAndCreate(TokenDTO tokenDTO)
      throws AuthenticationException, StatementException {
    TokenValidation.validateTokenDTO(tokenDTO);
    if (tokenDTO.getAccessToken().equalsIgnoreCase("")) {
      throw new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    }
    AuthCredentialDTO authCredentialDTO = tokenService.parseToken(tokenDTO);
    AuthCredentialInputDTO authCredentialInputDTO =
        new AuthCredentialInputDTO(
            authCredentialDTO.getUsername(), authCredentialDTO.getPassword());
    AuthCredentialEntity utente = authCredentialDAO.getCredential(authCredentialInputDTO);
    if (utente == null) {
      LOG.error("User Not Found, into StatementService, validateAndCreate(TokenDTO):96");
      throw new StatementException(StatementException.Code.USER_NOT_FOUND);
    }
    return utente;
  }
}

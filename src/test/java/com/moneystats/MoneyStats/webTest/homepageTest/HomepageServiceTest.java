package com.moneystats.MoneyStats.webTest.homepageTest;

import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
import com.moneystats.MoneyStats.web.homepage.HomepageService;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HomepageServiceTest {

  @Mock private IWalletDAO walletDAO;
  @Mock private IStatementDAO statementDAO;
  @Mock private AuthCredentialDAO authCredentialDAO;
  @Mock private TokenService tokenService;
  @InjectMocks private HomepageService homepageService;

  /**
   * Test Report Homepage
   *
   * @throws Exception
   */
  @Test
  public void test_statementReportHomepage_ok() throws Exception {
    HomepageReportDTO homepageReportDTO = DTOTestObjets.homepageReportDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = DTOTestObjets.statementEntityList;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(statementDAO.selectdistinctstatement(authCredentialEntity.getId()))
        .thenReturn(listDate);
    for (int i = 0; i < listDate.size(); i++) {
      Mockito.when(
              statementDAO.findAllByUserIdAndDateOrderByWalletId(
                  authCredentialEntity.getId(), listDate.get(i)))
          .thenReturn(statementEntityList);
    }
    Mockito.when(
            statementDAO.findAllByUserIdAndDateOrderByWalletId(
                authCredentialEntity.getId(), listDate.get(0)))
        .thenReturn(statementEntityList);

    HomepageReportDTO actual = homepageService.reportHomepage(tokenDTO);

    Assertions.assertEquals(homepageReportDTO.getStatementTotal(), actual.getStatementTotal());
    Assertions.assertEquals(
        homepageReportDTO.getStatementTotalPercent(), actual.getStatementTotalPercent());
    Assertions.assertEquals(homepageReportDTO.getPilPerformance(), actual.getPilPerformance());
    Assertions.assertEquals(homepageReportDTO.getPil(), actual.getPil());
    Assertions.assertEquals(homepageReportDTO.getPilTotal(), actual.getPilTotal());
    Assertions.assertEquals(homepageReportDTO.getLastDate(), actual.getLastDate());
    Assertions.assertEquals(homepageReportDTO.getBeforeLastDate(), actual.getBeforeLastDate());
    Assertions.assertEquals(homepageReportDTO.getFirstDate(), actual.getFirstDate());
    Assertions.assertEquals(homepageReportDTO.getListDate(), actual.getListDate());
    Assertions.assertEquals(homepageReportDTO.getStatementList(), actual.getStatementList());
    Assertions.assertEquals(homepageReportDTO.getListPil(), actual.getListPil());
  }

  @Test
  public void test_statementReportHomepage_shouldBeMappedOnInvalidToken() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> homepageService.reportHomepage(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  public void test_statementReportHomepage_shouldBeMappedOnTokenRequired() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> homepageService.reportHomepage(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  public void test_statementReportHomepage_shouldThowsOnListStatementNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);

    StatementException expectedException =
        new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND);
    StatementException actualException =
        Assertions.assertThrows(
            StatementException.class, () -> homepageService.reportHomepage(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  /**
   * Test Homepage Graph Data
   *
   * @throws Exception
   */
  @Test
  public void test_homepageGraph_ok() throws Exception {
    HomepagePieChartDTO expected = DTOTestObjets.homepagePieChartDTO;
    String date = DTOTestObjets.listDate.get(1);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = DTOTestObjets.statementEntityList;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(statementDAO.selectdistinctstatement(authCredentialEntity.getId()))
        .thenReturn(listDate);
    Mockito.when(
            statementDAO.findAllByUserIdAndDateOrderByWalletId(authCredentialEntity.getId(), date))
        .thenReturn(statementEntityList);
    Mockito.when(walletDAO.findAllByUserId(authCredentialEntity.getId()))
        .thenReturn(walletEntities);

    HomepagePieChartDTO actual = homepageService.getGraph(date, tokenDTO);

    Assertions.assertEquals(expected.getStatementList(), actual.getStatementList());
    Assertions.assertEquals(expected.getWalletList(), actual.getWalletList());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnInvalidToken() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(1);

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> homepageService.getGraph(date, tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnTokenRequired() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(1);

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> homepageService.getGraph(date, tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnStatementNotFound() throws Exception {
    String date = DTOTestObjets.listDate.get(1);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = DTOTestObjets.statementEntityList;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(statementDAO.selectdistinctstatement(authCredentialEntity.getId()))
        .thenReturn(listDate);

    StatementException expectedException =
        new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
    StatementException actualException =
        Assertions.assertThrows(
            StatementException.class, () -> homepageService.getGraph(date, tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnWalletNotFound() throws Exception {
    String date = DTOTestObjets.listDate.get(1);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = DTOTestObjets.statementEntityList;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(statementDAO.selectdistinctstatement(authCredentialEntity.getId()))
        .thenReturn(listDate);
    Mockito.when(
            statementDAO.findAllByUserIdAndDateOrderByWalletId(authCredentialEntity.getId(), date))
        .thenReturn(statementEntityList);

    WalletException expectedException = new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(
            WalletException.class, () -> homepageService.getGraph(date, tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }
}

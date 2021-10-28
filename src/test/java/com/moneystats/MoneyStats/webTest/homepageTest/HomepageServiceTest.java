package com.moneystats.MoneyStats.webTest.homepageTest;

import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementInputDTO;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletInputIdDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletStatementDTO;
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
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HomepageServiceTest {

  @Mock private IWalletDAO walletDAO;
  @Mock private IStatementDAO statementDAO;
  @Mock private AuthCredentialDAO authCredentialDAO;
  @Mock private TokenService tokenService;
  @InjectMocks private HomepageService homepageService;

  @Captor ArgumentCaptor<AuthCredentialInputDTO> authCredentialInputDTOArgumentCaptor;

  /**
   * Test Report Homepage
   *
   * @throws Exception
   */
  @Test
  public void test_statementReportHomepage_ok() throws Exception {
    HomepageReportDTO homepageReportDTO = DTOTestObjets.homepageReportDTO;
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = createValidStatementEntities();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);
    Mockito.when(statementDAO.selectdistinctstatement(authCredentialEntity.getId()))
        .thenReturn(listDate);
    for (int i = 0; i < listDate.size(); i++) {
      Mockito.when(
              statementDAO.findAllByUserIdAndDateOrderByWalletId(
                  authCredentialEntity.getId(), listDate.get(i)))
          .thenReturn(statementEntityList);
    }

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
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
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
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);

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
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = createValidStatementEntities();
    List<WalletEntity> walletEntities = createValidWalletEntities();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);
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
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
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
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<String> listDate = DTOTestObjets.listDate;

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);
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
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<String> listDate = DTOTestObjets.listDate;
    List<StatementEntity> statementEntityList = createValidStatementEntities();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);
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

  private StatementEntity createValidStatementEntity() {
    AuthCredentialEntity authCredentialEntity =
            new AuthCredentialEntity(
                    1L,
                    TestSchema.FIRSTNAME,
                    TestSchema.LASTNAME,
                    TestSchema.DATE_OF_BIRTH,
                    TestSchema.EMAIL,
                    TestSchema.STRING_USERNAME_ROLE_USER,
                    TestSchema.STRING_TOKEN_JWT_ROLE_USER,
                    SecurityRoles.MONEYSTATS_USER_ROLE);
    CategoryEntity categoryEntity = new CategoryEntity(1, "Category-name");
    WalletEntity walletEntity =
            new WalletEntity(1L, "my-wallet-1", categoryEntity, authCredentialEntity, null);
    return new StatementEntity("01-01-2021", 10.00D, authCredentialEntity, walletEntity);
  }

  private AuthCredentialDTO createValidAuthCredentialDTO() {
    return new AuthCredentialDTO(
            TestSchema.FIRSTNAME,
            TestSchema.LASTNAME,
            TestSchema.DATE_OF_BIRTH,
            TestSchema.EMAIL,
            TestSchema.STRING_USERNAME_ROLE_USER,
            TestSchema.STRING_TOKEN_JWT_ROLE_USER,
            SecurityRoles.MONEYSTATS_USER_ROLE);
  }

  private StatementInputDTO createValidStatementInputDTO() {
    return new StatementInputDTO(10.00D, "01-01-2021", 1L);
  }

  private List<WalletEntity> createValidWalletEntities() {
    List<WalletEntity> walletEntities =
            List.of(
                    new WalletEntity(
                            1L,
                            "my-wallet-1",
                            new CategoryEntity(1, "Credit Card"),
                            createValidStatementEntity().getUser(),
                            null),
                    new WalletEntity(
                            2L,
                            "my-wallet-2",
                            new CategoryEntity(1, "Credit Card"),
                            createValidStatementEntity().getUser(),
                            null),
                    new WalletEntity(
                            3L,
                            "my-wallet-3",
                            new CategoryEntity(1, "Credit Card"),
                            createValidStatementEntity().getUser(),
                            null),
                    new WalletEntity(
                            4L,
                            "my-wallet-4",
                            new CategoryEntity(1, "Credit Card"),
                            createValidStatementEntity().getUser(),
                            null));
    return walletEntities;
  }

  private WalletInputIdDTO createValidWalleInputDTO() {
    return new WalletInputIdDTO(1L, "my-wallet-1", 1);
  }

  private List<StatementEntity> createValidStatementEntities() {
    List<String> listDate = List.of("01-01-2021", "02-01-2021", "03-01-2021");
    List<StatementEntity> statementEntityList =
            List.of(
                    new StatementEntity(
                            listDate.get(0),
                            250.00,
                            createValidStatementEntity().getUser(),
                            createValidWalletEntities().get(0)),
                    new StatementEntity(
                            listDate.get(0),
                            250.00,
                            createValidStatementEntity().getUser(),
                            createValidWalletEntities().get(1)),
                    new StatementEntity(
                            listDate.get(0),
                            250.00,
                            createValidStatementEntity().getUser(),
                            createValidWalletEntities().get(2)),
                    new StatementEntity(
                            listDate.get(0),
                            250.00,
                            createValidStatementEntity().getUser(),
                            createValidWalletEntities().get(3)));
    return statementEntityList;
  }

  private WalletStatementDTO createValidWalletStatementDTO() {
    WalletStatementDTO walletStatementDTO =
            new WalletStatementDTO(createValidWalletEntities(), createValidStatementEntities());
    return walletStatementDTO;
  }

  private List<WalletDTO> createValidWalletDTOS() {
    return List.of(
            new WalletDTO(
                    createValidWalletEntities().get(0).getName(),
                    createValidWalletEntities().get(0).getCategory(),
                    createValidWalletEntities().get(0).getUser()),
            new WalletDTO(
                    createValidWalletEntities().get(1).getName(),
                    createValidWalletEntities().get(1).getCategory(),
                    createValidWalletEntities().get(1).getUser()),
            new WalletDTO(
                    createValidWalletEntities().get(2).getName(),
                    createValidWalletEntities().get(2).getCategory(),
                    createValidWalletEntities().get(2).getUser()),
            new WalletDTO(
                    createValidWalletEntities().get(3).getName(),
                    createValidWalletEntities().get(3).getCategory(),
                    createValidWalletEntities().get(3).getUser()));
  }
}

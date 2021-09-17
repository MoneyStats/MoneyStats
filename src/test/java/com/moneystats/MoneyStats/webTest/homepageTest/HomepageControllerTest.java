package com.moneystats.MoneyStats.webTest.homepageTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
import com.moneystats.MoneyStats.web.homepage.HomepageController;
import com.moneystats.MoneyStats.web.homepage.HomepageService;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = HomepageController.class)
public class HomepageControllerTest {

  @MockBean private HomepageService homepageService;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  /**
   * Test Report Homepage Data
   *
   * @throws Exception
   */
  @Test
  public void test_statementReportHomepage_ok() throws Exception {
    HomepageReportDTO homepageReportDTO = DTOTestObjets.homepageReportDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(homepageService.reportHomepage(tokenDTO)).thenReturn(homepageReportDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/homepage/reportHomepage")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void test_statementReportHomepage_shouldBeMappedOnInvalidToken() throws Exception {
    HomepageReportDTO homepageReportDTO = DTOTestObjets.homepageReportDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(homepageService.reportHomepage(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/homepage/reportHomepage"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void test_statementReportHomepage_shouldBeMappedOnTokenRequired() throws Exception {
    TokenDTO tokenDTO = new TokenDTO("");

    Mockito.when(homepageService.reportHomepage(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/homepage/reportHomepage"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void test_statementReportHomepage_shouldThowsOnListStatementNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(homepageService.reportHomepage(Mockito.any()))
        .thenThrow(new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/homepage/reportHomepage")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Test Homepage Get Graph
   *
   * @throws Exception
   */
  @Test
  public void test_homepageGraph_ok() throws Exception {
    HomepagePieChartDTO expected = DTOTestObjets.homepagePieChartDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(0);

    Mockito.when(homepageService.getGraph(date, tokenDTO)).thenReturn(expected);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/homepage/getPieGraph/" + date)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnInvalidToken() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(0);

    Mockito.when(homepageService.getGraph(date, tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/homepage/getPieGraph/" + date))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnTokenRequired() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(0);

    Mockito.when(homepageService.getGraph(date, tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/homepage/getPieGraph/" + date))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnStatementNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(0);

    Mockito.when(homepageService.getGraph(Mockito.any(), Mockito.any()))
        .thenThrow(new StatementException(StatementException.Code.STATEMENT_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/homepage/getPieGraph/" + date)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void test_homepageGraph_shouldBeMappedOnWalletNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String date = DTOTestObjets.listDate.get(0);

    Mockito.when(homepageService.getGraph(Mockito.any(), Mockito.any()))
        .thenThrow(new WalletException(WalletException.Code.WALLET_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/homepage/getPieGraph/" + date)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}

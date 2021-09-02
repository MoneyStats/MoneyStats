package com.moneystats.MoneyStats.statementTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementResponseDTO;
import com.moneystats.MoneyStats.commStats.statement.StatementController;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.StatementService;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.SchemaDescription;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = StatementController.class)
public class StatementControllerTest {

  @MockBean private StatementService statementService;
  @Autowired private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testAddStatement_OK() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    StatementDTO statementDTO = DTOTestObjets.statementDTO;
    StatementResponseDTO statementResponseDTO =
        new StatementResponseDTO(SchemaDescription.STATEMENT_ADDED_CORRECT);
    String statementAsString = objectMapper.writeValueAsString(statementDTO);

    Mockito.when(statementService.addStatement(tokenDTO, statementDTO))
        .thenReturn(statementResponseDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/statement/addStatement")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(statementAsString))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testAddStatement_shouldBeMappedOnError() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    StatementDTO statementDTO = DTOTestObjets.statementDTO;
    String statementAsString = objectMapper.writeValueAsString(statementDTO);
    statementDTO.setValue(null);
    statementDTO.setDate(null);

    Mockito.when(statementService.addStatement(tokenDTO, statementDTO))
        .thenThrow(new StatementException(StatementException.Code.INVALID_STATEMENT_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/statement/addStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statementAsString))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testAddStatement_shouldBeMappedOnTokenRequired() throws Exception {
    TokenDTO tokenDTO = new TokenDTO("");
    StatementDTO statementDTO = DTOTestObjets.statementDTO;
    String statementAsString = objectMapper.writeValueAsString(statementDTO);

    Mockito.when(statementService.addStatement(tokenDTO, statementDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/statement/addStatement")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statementAsString))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testListOfDate_OK() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    List<String> date = List.of("my-date");
    String dateAsString = objectMapper.writeValueAsString(date);

    Mockito.when(statementService.listOfDate(tokenDTO)).thenReturn(date);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/statement/listOfDate")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testListOfDate_shouldBeMappedOnError() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    List<String> date = List.of("my-date");
    String dateAsString = objectMapper.writeValueAsString(date);

    Mockito.when(statementService.listOfDate(tokenDTO))
        .thenThrow(new StatementException(StatementException.Code.INVALID_STATEMENT_DTO));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/statement/listOfDate"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testListOfStatementByDate_OK() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    List<StatementEntity> statementEntityList = DTOTestObjets.statementEntityList;
    String date = "2021-06-09";

    Mockito.when(statementService.listStatementByDate(tokenDTO, date))
        .thenReturn(statementEntityList);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/statement/listStatementDate/" + date)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}

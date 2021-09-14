package com.moneystats.MoneyStats.walletTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.category.CategoryException;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.DTO.*;
import com.moneystats.MoneyStats.commStats.wallet.WalletController;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.WalletService;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.ws.rs.core.MediaType;
import java.util.List;

@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {

  @MockBean private WalletService walletService;
  @Autowired private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  /**
   * getAll Test
   *
   * @throws Exception
   */
  @Test
  public void testGetAllWalletList_OK() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    List<WalletEntity> walletDTOS = DTOTestObjets.walletEntities;
    String walletAsString = objectMapper.writeValueAsString(walletDTOS);

    Mockito.when(walletService.getAll(Mockito.any())).thenReturn(walletDTOS);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/list")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(walletAsString));
  }

  @Test
  public void testGetAllWalletList_shouldBeMappedOnInvalidTokenDTO() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.getAll(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/list")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testGetAllWalletList_shouldBeMappedOnTokenDTORequired() throws Exception {
    TokenDTO tokenDTO = new TokenDTO("");
    List<WalletDTO> walletDTOS = DTOTestObjets.walletDTOS;

    Mockito.when(walletService.getAll(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/list")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testGetAllWalletList_shouldBeMappedOnWalletNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    List<WalletDTO> walletDTOS = DTOTestObjets.walletDTOS;

    Mockito.when(walletService.getAll(Mockito.any()))
        .thenThrow(new WalletException(WalletException.Code.WALLET_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/list")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * AddWalletTest
   *
   * @throws Exception
   */
  @Test
  public void testAddWallet_OK() throws Exception {
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    String walletDTOasString = objectMapper.writeValueAsString(walletDTO);
    WalletResponseDTO response =
        new WalletResponseDTO(SchemaDescription.WALLET_ADDED_CORRECT.toString());
    String responseAsString = objectMapper.writeValueAsString(response);

    Mockito.when(walletService.addWalletEntity(Mockito.any(), Mockito.any())).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header(
                    "Authorization",
                    "Bearer " + TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(responseAsString));
  }

  @Test
  public void testAddWallet_shouldBeMappedOnError() throws Exception {
    WalletInputDTO walletDTO = DTOTestObjets.walletInputDTO;
    walletDTO.setName(null);

    Mockito.when(walletService.addWalletEntity(TestSchema.TOKEN_JWT_DTO_ROLE_USER, walletDTO))
        .thenThrow(new WalletException(WalletException.Code.INVALID_WALLET_INPUT_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet")
                .header(
                    "Authorization",
                    "Bearer " + TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testAddWalletEntity_shouldBeMappedOnInvalidTokenDTO() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.addWalletEntity(Mockito.any(), Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testAddWalletEntity_shouldBeMappedOnTokenDTORequired() throws Exception {
    TokenDTO tokenDTO = new TokenDTO("");

    Mockito.when(walletService.addWalletEntity(Mockito.any(), Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testAddWalletEntity_shouldBeMappedCategoryNotFound() throws Exception {
    WalletInputDTO walletDTO = DTOTestObjets.walletInputDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String walletDTOasString = objectMapper.writeValueAsString(walletDTO);

    Mockito.when(walletService.addWalletEntity(Mockito.any(), Mockito.any()))
        .thenThrow(new CategoryException(CategoryException.Code.CATEGORY_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * deleteWalletTest
   *
   * @throws Exception
   */
  @Test
  public void testDeleteWallet_OK() throws Exception {
    Long idWallet = 1L;
    WalletResponseDTO response =
        new WalletResponseDTO(SchemaDescription.WALLET_DELETE_CORRECT.toString());
    String responseAsString = objectMapper.writeValueAsString(response);

    Mockito.when(walletService.deleteWalletEntity(idWallet)).thenReturn(response);

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/wallet/delete/" + idWallet))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(responseAsString));
  }

  @Test
  public void testDeleteWallet_shouldReturnWalletNotFound() throws Exception {
    Long idWallet = 1L;

    Mockito.when(walletService.deleteWalletEntity(idWallet))
        .thenThrow(new WalletException(WalletException.Code.WALLET_NOT_FOUND));

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/wallet/delete/" + idWallet))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * test myWalletMobile Data
   *
   * @throws Exception
   */
  @Test
  public void testWalletListMobile_ok() throws Exception {
    WalletStatementDTO response = DTOTestObjets.walletStatementDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(tokenDTO)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testWalletListMobile_shouldBeMappedOnInvalidTokenDTO() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testWalletListMobile_shouldBeMappedOnRequiredTokenDTO() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testWalletListMobile_shouldBeMappedOnListStatementNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(Mockito.any()))
        .thenThrow(new StatementException(StatementException.Code.LIST_STATEMENT_DATE_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void testWalletListMobile_shouldBeMappedOnStatementNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(Mockito.any()))
        .thenThrow(new StatementException(StatementException.Code.STATEMENT_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void testWalletListMobile_shouldBeMappedOnWalletNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.myWalletMobile(Mockito.any()))
        .thenThrow(new WalletException(WalletException.Code.WALLET_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/listMobile")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * test editWallet
   *
   * @throws Exception
   */
  @Test
  public void testEditWallet_OK() throws Exception {
    WalletInputIdDTO walletInputIdDTO = DTOTestObjets.walletInputIdDTO;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    String walletDTOasString = objectMapper.writeValueAsString(walletInputIdDTO);
    WalletResponseDTO response =
        new WalletResponseDTO(SchemaDescription.WALLET_EDIT_CORRECT.toString());

    Mockito.when(walletService.editWallet(walletInputIdDTO, tokenDTO)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/wallet/editWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testEditWallet_shouldBeMappedOnInvalidWalletInputIdDTO() throws Exception {
    WalletInputIdDTO walletInputIdDTO = DTOTestObjets.walletInputIdDTO;
    walletInputIdDTO.setName(null);
    String walletDTOasString = objectMapper.writeValueAsString(walletInputIdDTO);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.editWallet(Mockito.any(), Mockito.any()))
        .thenThrow(new WalletException(WalletException.Code.INVALID_WALLET_INPUT_ID_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/wallet/editWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testEditWallet_shouldBeMappedOnInvalidTokenDTO() throws Exception {
    WalletInputIdDTO walletInputIdDTO = DTOTestObjets.walletInputIdDTO;
    walletInputIdDTO.setName(null);
    String walletDTOasString = objectMapper.writeValueAsString(walletInputIdDTO);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.editWallet(Mockito.any(), Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/wallet/editWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testEditWallet_shouldBeMappedOnRequiredTokenDTO() throws Exception {
    WalletInputIdDTO walletInputIdDTO = DTOTestObjets.walletInputIdDTO;
    String walletDTOasString = objectMapper.writeValueAsString(walletInputIdDTO);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    tokenDTO.setAccessToken("");

    Mockito.when(walletService.editWallet(Mockito.any(), Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/wallet/editWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void testEditWallet_shouldBeMappedOnCategoryNotFound() throws Exception {
    WalletInputIdDTO walletInputIdDTO = DTOTestObjets.walletInputIdDTO;
    String walletDTOasString = objectMapper.writeValueAsString(walletInputIdDTO);
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(walletService.editWallet(Mockito.any(), Mockito.any()))
        .thenThrow(new CategoryException(CategoryException.Code.CATEGORY_NOT_FOUND));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/wallet/editWallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * test GetById
   *
   * @throws Exception
   */
  @Test
  public void testgetByIdWallet_OK() throws Exception {
    Long idWallet = 1L;
    WalletDTO response = DTOTestObjets.walletDTO;

    Mockito.when(walletService.walletById(Mockito.any())).thenReturn(response);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/wallet/getById/" + idWallet))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testgetByIdWallet_shouldBeMappedOnWalletNotFound() throws Exception {
    Long idWallet = 1L;
    WalletDTO response = DTOTestObjets.walletDTO;

    Mockito.when(walletService.walletById(Mockito.any()))
        .thenThrow(new WalletException(WalletException.Code.WALLET_NOT_FOUND));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/wallet/getById/" + idWallet))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}

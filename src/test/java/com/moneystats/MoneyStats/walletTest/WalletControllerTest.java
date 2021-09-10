package com.moneystats.MoneyStats.walletTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.MoneyStats.commStats.wallet.WalletController;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.WalletService;
import com.moneystats.MoneyStats.source.DTOTestObjets;
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
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {

  @MockBean private WalletService walletService;
  @Autowired private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testGetAllWalletList_OK() throws Exception {
    List<WalletDTO> walletDTOS = DTOTestObjets.walletDTOS;
    String walletAsString = objectMapper.writeValueAsString(walletDTOS);
    Mockito.when(walletService.getAll(TestSchema.TOKEN_JWT_DTO_ROLE_USER)).thenReturn(walletDTOS);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/wallet/list")
                .header(
                    "Authorization",
                    "Bearer " + TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testAddWallet_OK() throws Exception {
    Integer idCategory = 1;
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    String walletDTOasString = objectMapper.writeValueAsString(walletDTO);
    WalletResponseDTO response =
        new WalletResponseDTO(SchemaDescription.WALLET_ADDED_CORRECT.toString());

    Mockito.when(
            walletService.addWalletEntity(
                TestSchema.TOKEN_JWT_DTO_ROLE_USER, idCategory, walletDTO))
        .thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/addWallet/" + idCategory)
                .contentType(MediaType.APPLICATION_JSON)
                .content(walletDTOasString)
                .header(
                    "Authorization",
                    "Bearer " + TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testAddWallet_shouldBeMappedOnError() throws Exception {
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    walletDTO.setName(null);
    int idCategory = 1;

    Mockito.when(
            walletService.addWalletEntity(
                TestSchema.TOKEN_JWT_DTO_ROLE_USER, idCategory, walletDTO))
        .thenThrow(new WalletException(WalletException.Code.INVALID_WALLET_DTO));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/wallet/postWallet/" + idCategory)
                .header(
                    "Authorization",
                    "Bearer " + TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void testAddWallet_shouldBeMappedUserNotFound() throws Exception {
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = 1;

    Mockito.when(
            walletService.addWalletEntity(
                TestSchema.TOKEN_JWT_DTO_ROLE_USER, idCategory, walletDTO))
        .thenThrow(new WalletException(WalletException.Code.USER_NOT_FOUND));

    mockMvc
        .perform(MockMvcRequestBuilders.post("/wallet/postWallet"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void testAddWalletList_shouldBeMappedWalletDtoNull() throws Exception {
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = null;

    Mockito.when(
            walletService.addWalletEntity(
                TestSchema.TOKEN_JWT_DTO_ROLE_USER, idCategory, walletDTO))
        .thenThrow(new WalletException(WalletException.Code.CATEGORY_NOT_FOUND));

    mockMvc
        .perform(MockMvcRequestBuilders.post("/wallet/postWallet"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

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

  @Test
  public void testDeleteWallet_shouldReturnCategoryNotFound() throws Exception {
    Long idWallet = 1L;

    Mockito.when(walletService.deleteWalletEntity(idWallet))
        .thenThrow(new WalletException(WalletException.Code.CATEGORY_NOT_FOUND));

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/wallet/delete/" + idWallet))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}

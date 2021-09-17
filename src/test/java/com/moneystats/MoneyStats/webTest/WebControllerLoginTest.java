package com.moneystats.MoneyStats.webTest;

import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.MoneyStats.web.WebControllerLogin;
import com.moneystats.MoneyStats.web.WebException;
import com.moneystats.MoneyStats.web.WebExceptionMapper;
import com.moneystats.MoneyStats.web.WebService;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
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

@WebMvcTest(controllers = WebControllerLogin.class)
public class WebControllerLoginTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private WebService webService;

  /**
   * Test to checkLogin User
   * @throws Exception
   */
  @Test
  public void testCheckLogin() throws Exception {
    AuthCredentialDTO authCredentialDTO = DTOTestObjets.authCredentialDTO;
    TokenDTO tokenDTO = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT);

    Mockito.when(webService.checkUser(tokenDTO)).thenReturn(authCredentialDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/check_login")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testCheckLogin_shoudBeMappedOnError() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    Mockito.when(webService.checkUser(tokenDTO)).thenThrow(new WebException(WebException.Code.LOGIN_REQUIRED));

    mockMvc
            .perform(
                    MockMvcRequestBuilders.get("/check_login"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}

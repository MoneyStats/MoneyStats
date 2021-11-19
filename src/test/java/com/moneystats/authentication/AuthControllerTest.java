package com.moneystats.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.authentication.DTO.*;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.ResponseMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthCredentialController.class)
public class AuthControllerTest {

  @MockBean private AuthCredentialService credential;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void addUser_shouldReturnStatus200AndReturnResponse() throws Exception {
    String userAsString = objectMapper.writeValueAsString(TestSchema.USER_CREDENTIAL_DTO);
    String authResponseAsString = objectMapper.writeValueAsString(TestSchema.AUTH_RESPONSE_DTO);

    Mockito.when(credential.signUp(TestSchema.USER_CREDENTIAL_DTO))
        .thenReturn(TestSchema.AUTH_RESPONSE_DTO);
    mockMvc
        .perform(
            post("/credential/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsString))
        .andExpect(status().isOk());
  }

  @Test
  void addUser_shouldReturnError400OnBadInput() throws Exception {
    Mockito.doThrow(
            new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO))
        .when(credential)
        .signUp(Mockito.any());
    mockMvc
        .perform(
            post("/credential/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\": 30, \"password\": 90}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addUser_shouldReturnUserPresent() throws Exception {
    AuthCredentialDTO user = TestSchema.USER_CREDENTIAL_DTO;
    String userAsString = objectMapper.writeValueAsString(user);

    Mockito.when(credential.signUp(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_PRESENT));

    mockMvc
        .perform(
            post("/credential/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsString))
        .andExpect(status().isBadRequest());
  }

  @Test
  void addUser_shouldReturnEmailPresent() throws Exception {
    AuthCredentialDTO user = TestSchema.USER_CREDENTIAL_DTO;
    String userAsString = objectMapper.writeValueAsString(user);

    Mockito.when(credential.signUp(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.EMAIL_PRESENT));

    mockMvc
        .perform(
            post("/credential/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userAsString))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_shouldReturnTokenCorrectly() throws Exception {
    String userAsString =
        objectMapper.writeValueAsString(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER);
    String tokenAsString = objectMapper.writeValueAsString(TestSchema.TOKEN_JWT_DTO_ROLE_USER);

    Mockito.doReturn(TestSchema.TOKEN_JWT_DTO_ROLE_USER).when(credential).login(Mockito.any());
    mockMvc
        .perform(
            post("/credential/login").contentType(MediaType.APPLICATION_JSON).content(userAsString))
        .andExpect(status().isOk())
        .andExpect(content().json(tokenAsString));
  }

  @Test
  void login_shouldReturnError400OnBadInput() throws Exception {
    Mockito.doThrow(
            new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_INPUT_DTO))
        .when(credential)
        .login(Mockito.any());
    mockMvc
        .perform(
            post("/credential/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user1\": \"giovanni\", \"password\": 90}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void login_shouldReturnWrongcredentialOnUsername() throws Exception {
    AuthCredentialInputDTO user =
        new AuthCredentialInputDTO(
            TestSchema.STRING_USERNAME_ROLE_USER, TestSchema.STRING_PASSWORD_ROLE_USER);
    user.setUsername("wrong-username");
    String userAsString = objectMapper.writeValueAsString(user);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL))
        .when(credential)
        .login(Mockito.any());
    mockMvc
        .perform(
            post("/credential/login").contentType(MediaType.APPLICATION_JSON).content(userAsString))
        .andExpect(status().is(401));
  }

  @Test
  void login_shouldReturnWrongcredentialOnPassword() throws Exception {
    AuthCredentialInputDTO user =
        new AuthCredentialInputDTO(TestSchema.STRING_USERNAME_ROLE_USER, TestSchema.WRONG_PASSWORD);
    String userAsString = objectMapper.writeValueAsString(user);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL))
        .when(credential)
        .login(Mockito.any());
    mockMvc
        .perform(
            post("/credential/login").contentType(MediaType.APPLICATION_JSON).content(userAsString))
        .andExpect(status().is(401));
  }

  @Test
  void tokenUser_shouldReturnUser() throws Exception {
    String userAsString = objectMapper.writeValueAsString(TestSchema.USER_CREDENTIAL_DTO);

    Mockito.doReturn(TestSchema.USER_CREDENTIAL_DTO).when(credential).getUser(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/token")
                .header("Authorization", "Bearer " + TestSchema.STRING_TOKEN_JWT_ROLE_USER))
        .andExpect(status().isOk())
        .andExpect(content().json(userAsString));
  }

  @Test
  void tokenUser_shouldReturnUnauthorized() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED))
        .when(credential)
        .getUser(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/token")
                .header("Authorization", "Bearer " + token.getAccessToken()))
        .andExpect(status().is(401));
  }

  @Test
  void tokenUser_shouldReturnInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO))
        .when(credential)
        .getUser(token);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/credential/token"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void listUsers_adminShouldReceiveTheListCorrectly() throws Exception {
    List<AuthCredentialDTO> listUsers =
        List.of(TestSchema.USER_CREDENTIAL_DTO, TestSchema.USER_CREDENTIAL_DTO);
    String usersAsString = objectMapper.writeValueAsString(listUsers);

    Mockito.doReturn(listUsers).when(credential).getUsers(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/admin")
                .header("Authorization", "Bearer " + TestSchema.STRING_TOKEN_JWT_ROLE_USER))
        .andExpect(status().isOk())
        .andExpect(content().json(usersAsString));
  }

  @Test
  void listUsers_adminShouldReturnNotAllowed() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED))
        .when(credential)
        .getUsers(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/admin")
                .header("Authorization", "Bearer " + token.getAccessToken()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void listUsers_adminShouldReturnInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);

    Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO))
        .when(credential)
        .getUsers(token);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/credential/admin"))
        .andExpect(status().isBadRequest());
  }

  /** Test Update User */
  @Test
  void updateUser_shouldUpdateUsercorrectly() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialToUpdateDTO authCredentialToUpdateDTOExpected =
        TestSchema.AUTH_CREDENTIAL_TO_UPDATE_DTO;
    String authCredentialToUpdateDTOAsString =
        objectMapper.writeValueAsString(authCredentialToUpdateDTOExpected);
    AuthResponseDTO authResponseDTOExpected = new AuthResponseDTO(AuthResponseDTO.String.USER_UPDATED);

    Mockito.when(credential.updateUser(authCredentialToUpdateDTOExpected, tokenDTO))
        .thenReturn(authResponseDTOExpected);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update")
                .header("Authorization", "Bearer " + TestSchema.STRING_TOKEN_JWT_ROLE_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(authCredentialToUpdateDTOAsString))
        .andExpect(status().isOk());
  }

  @Test
  void updateUser_shouldThrowsOnInvalidTokenDTO() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTOExpected =
        TestSchema.AUTH_CREDENTIAL_TO_UPDATE_DTO;
    String authCredentialToUpdateDTOAsString =
        objectMapper.writeValueAsString(authCredentialToUpdateDTOExpected);

    Mockito.when(credential.updateUser(authCredentialToUpdateDTOExpected, tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authCredentialToUpdateDTOAsString))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUser_shouldThrowsOnTokenDTONotFound() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTOExpected =
        TestSchema.AUTH_CREDENTIAL_TO_UPDATE_DTO;
    String authCredentialToUpdateDTOAsString =
        objectMapper.writeValueAsString(authCredentialToUpdateDTOExpected);

    Mockito.when(credential.updateUser(authCredentialToUpdateDTOExpected, tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authCredentialToUpdateDTOAsString))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUser_shouldThrowsOnInvalidAuthcredentialToUpdateDTO() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTOExpected = null;
    String authCredentialToUpdateDTOAsString = objectMapper.writeValueAsString(null);

    Mockito.when(credential.updateUser(authCredentialToUpdateDTOExpected, tokenDTO))
        .thenThrow(
            new AuthenticationException(
                AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_TO_UPDATE_DTO));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update")
                .header("Authorization", "Bearer " + TestSchema.STRING_TOKEN_JWT_ROLE_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(authCredentialToUpdateDTOAsString))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateUser_shouldThrowsOnUserDontMatch() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTOExpected = null;
    String authCredentialToUpdateDTOAsString = objectMapper.writeValueAsString(null);

    Mockito.when(credential.updateUser(authCredentialToUpdateDTOExpected, tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update")
                .header("Authorization", "Bearer " + TestSchema.STRING_TOKEN_JWT_ROLE_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(authCredentialToUpdateDTOAsString))
        .andExpect(status().isBadRequest());
  }

  /** Test getUpdateUser */
  @Test
  void getUpdateUser_shouldGetUserCorrectly() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTOExpected = TestSchema.USER_CREDENTIAL_DTO;

    Mockito.when(credential.getUpdateUser(tokenDTO)).thenReturn(authCredentialDTOExpected);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/getCurrentUser")
                .header("Authorization", "Bearer " + tokenDTO))
        .andExpect(status().isOk());
  }

  @Test
  void getUpdateUser_shouldThrowOnUserDontMatch() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);

    Mockito.when(credential.getUpdateUser(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/getCurrentUser")
                .header("Authorization", "Bearer " + tokenDTO))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getUpdateUser_shouldThrowOnInvalidToken() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(null);

    Mockito.when(credential.getUpdateUser(Mockito.any()))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/getCurrentUser")
                .header("Authorization", "Bearer " + tokenDTO))
        .andExpect(status().isBadRequest());
  }

  /** Test getUpdateUser */
  @Test
  void updatePassword_shouldUpdatePasswordCorrectly() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthResponseDTO response = new AuthResponseDTO(AuthResponseDTO.String.PASSWORD_UPDATED);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
        TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    String authChangePasswordInputDTOAsString =
        objectMapper.writeValueAsString(authChangePasswordInputDTO);

    Mockito.when(credential.updatePassword(authChangePasswordInputDTO, tokenDTO))
        .thenReturn(response);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/credential/update/password")
                .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(authChangePasswordInputDTOAsString))
        .andExpect(status().isOk());
  }

  @Test
  void updatePassword_shouldThrowsOnInvalidToken() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(null);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
            TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    String authChangePasswordInputDTOAsString =
            objectMapper.writeValueAsString(authChangePasswordInputDTO);

    Mockito.when(credential.updatePassword(authChangePasswordInputDTO, tokenDTO))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/credential/update/password")
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authChangePasswordInputDTOAsString))
            .andExpect(status().isOk());
  }

  @Test
  void updatePassword_shouldThrowsOnInvalidAuthChangePasswordInput() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
            TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    authChangePasswordInputDTO.setConfirmNewPassword(null);

    Mockito.when(credential.updatePassword(authChangePasswordInputDTO, tokenDTO))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CHANGE_PASSWORD_INPUT_DTO));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/credential/update/password")
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken()))
            .andExpect(status().isBadRequest());
  }

  @Test
  void updatePassword_shouldThrowsOnPasswordDontMatch() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
            TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    authChangePasswordInputDTO.setNewPassword("my-password");
    String authChangePasswordInputDTOAsString =
            objectMapper.writeValueAsString(authChangePasswordInputDTO);

    Mockito.when(credential.updatePassword(Mockito.any(), Mockito.any()))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.PASSWORD_NOT_MATCH));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/credential/update/password")
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authChangePasswordInputDTOAsString))
            .andExpect(status().isBadRequest());
  }

  @Test
  void updatePassword_shouldThrowsOnUserDontMatch() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
            TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    authChangePasswordInputDTO.setUsername("new-user");
    String authChangePasswordInputDTOAsString =
            objectMapper.writeValueAsString(authChangePasswordInputDTO);

    Mockito.when(credential.updatePassword(Mockito.any(), Mockito.any()))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/credential/update/password")
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authChangePasswordInputDTOAsString))
            .andExpect(status().isBadRequest());
  }

  @Test
  void updatePassword_shouldThrowsOnWrongCredential() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO =
            TestSchema.AUTH_CHANGE_PASSWORD_INPUT_DTO;
    authChangePasswordInputDTO.setOldPassword("wrong-password");
    String authChangePasswordInputDTOAsString =
            objectMapper.writeValueAsString(authChangePasswordInputDTO);

    Mockito.when(credential.updatePassword(Mockito.any(), Mockito.any()))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL));
    mockMvc
            .perform(
                    MockMvcRequestBuilders.put("/credential/update/password")
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authChangePasswordInputDTOAsString))
            .andExpect(status().isUnauthorized());
  }
}

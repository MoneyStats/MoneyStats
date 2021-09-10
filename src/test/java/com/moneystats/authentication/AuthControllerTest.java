package com.moneystats.authentication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;

@WebMvcTest(controllers = AuthCredentialController.class)
public class AuthControllerTest {

  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @MockBean private AuthCredentialService credential;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void addUser_shouldReturnStatus200AndReturnResponse() throws Exception {
    String userAsString = objectMapper.writeValueAsString(TestSchema.USER_USER_DTO);
    String authResponseAsString = objectMapper.writeValueAsString(TestSchema.AUTH_RESPONSE_DTO);

    Mockito.when(credential.signUp(TestSchema.USER_USER_DTO)).thenReturn(TestSchema.AUTH_RESPONSE_DTO);
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
    AuthCredentialDTO user = TestSchema.USER_USER_DTO;
    String userAsString = objectMapper.writeValueAsString(user);

    Mockito.when(credential.signUp(Mockito.any())).thenThrow(new AuthenticationException(AuthenticationException.Code.USER_PRESENT));

    mockMvc
            .perform(
                    post("/credential/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userAsString))
            .andExpect(status().isBadRequest());
  }

  @Test
  void login_shouldReturnTokenCorrectly() throws Exception {
    String userAsString = objectMapper.writeValueAsString(TestSchema.USER_USER_CREDENTIAL_DTO);
    String tokenAsString = objectMapper.writeValueAsString(TestSchema.USER_TOKEN_JWT);

    Mockito.doReturn(TestSchema.USER_TOKEN_JWT).when(credential).login(Mockito.any());
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
            new AuthCredentialInputDTO(TestSchema.USER_USERNAME_WRONG, TestSchema.USER_PASSWORD);
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
        new AuthCredentialInputDTO(TestSchema.USER_USERNAME, TestSchema.WRONG_PASSWORD);
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
    String userAsString = objectMapper.writeValueAsString(TestSchema.USER_USER_DTO);

    Mockito.doReturn(TestSchema.USER_USER_DTO).when(credential).getUser(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/token")
                .header("Authorization", "Bearer " + TestSchema.USER_JWT))
        .andExpect(status().isOk())
        .andExpect(content().json(userAsString));
  }

  @Test
  void tokenUser_shouldReturnUnauthorized() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.USER_JWT_WRONG);

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
    List<AuthCredentialDTO> listUsers = List.of(TestSchema.USER_USER_DTO, TestSchema.USER_USER_DTO);
    String usersAsString = objectMapper.writeValueAsString(listUsers);

    Mockito.doReturn(listUsers).when(credential).getUsers(Mockito.any());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/credential/admin")
                .header("Authorization", "Bearer " + TestSchema.USER_JWT))
        .andExpect(status().isOk())
        .andExpect(content().json(usersAsString));
  }

  @Test
  void listUsers_adminShouldReturnNotAllowed() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.USER_JWT_WRONG);

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
}

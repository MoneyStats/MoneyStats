package com.moneystats.authentication;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.SchemaDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SpringBootTest
public class AuthServiceTest {

  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @Mock private AuthCredentialDAO dao;

  @InjectMocks private AuthCredentialService service;

  @Mock TokenService tokenService;

  @Mock HttpServletRequest httpServletRequest;

  @Captor ArgumentCaptor<AuthCredentialInputDTO> authCredentialInputDTOArgumentCaptor;

  @Captor ArgumentCaptor<TokenDTO> tokenDTOCaptor;

  @Captor ArgumentCaptor<AuthCredentialDTO> authCredentialDTOArgumentCaptor;

  @Autowired private static TestSchema testSchema;

  @BeforeEach
  void setPassword_setDefaultPassword() {
    TestSchema.USER_CREDENTIAL_DTO_ROLE_USER.setPassword(TestSchema.ROLE_USER_PASSWORD);
  }

  @Test
  void signUpUser_shouldInsertUserCorrectlyAndReturnUserAdded() throws Exception {
    Mockito.doNothing().when(dao).insertUserCredential(authCredentialDTOArgumentCaptor.capture());

    AuthResponseDTO response = service.signUp(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);

    Assertions.assertTrue(
        bCryptPasswordEncoder.matches(
            TestSchema.ROLE_USER_PASSWORD,
            authCredentialDTOArgumentCaptor.getValue().getPassword()));
    Assertions.assertEquals(
        TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER.getUsername(),
        authCredentialDTOArgumentCaptor.getValue().getUsername());
    Assertions.assertEquals(SchemaDescription.USER_ADDED_CORRECT, response.getMessage());
  }

  @Test
  void signUpUser_shouldReturnInvalidInput() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.ROLE_USER_USERNAME, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.signUp(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void signUpUser_shouldReturnUserPresent() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.ROLE_USER_USERNAME, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_PRESENT);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.signUp(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void signUpUser_shouldReturEmailPresent() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.ROLE_USER_USERNAME, null);

    AuthenticationException expected =
            new AuthenticationException(AuthenticationException.Code.EMAIL_PRESENT);
    AuthenticationException actual =
            Assertions.assertThrows(AuthenticationException.class, () -> service.signUp(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void login_shouldReturnTokenCorrectly() throws Exception {
    Mockito.when(dao.getCredential(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER))
        .thenReturn(TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER);
    Mockito.when(tokenService.generateToken(authCredentialDTOArgumentCaptor.capture()))
        .thenReturn(TestSchema.TOKEN_JWT_DTO_ROLE_USER);
    Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("remote-address");

    TokenDTO actual = service.login(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER);

    Assertions.assertEquals(
        TestSchema.TOKEN_JWT_DTO_ROLE_USER.getAccessToken(), actual.getAccessToken());
  }

  @Test
  void login_shouldThrowWrongCredential() throws Exception {
    Mockito.when(dao.getCredential(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER))
        .thenReturn(null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.login(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void login_shouldThrowInvalidInput() throws Exception {
    AuthCredentialInputDTO user = new AuthCredentialInputDTO(TestSchema.ROLE_USER_USERNAME, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_INPUT_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.login(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void login_shouldThrowWrongPassword() throws Exception {
    AuthCredentialInputDTO user =
        new AuthCredentialInputDTO(
            TestSchema.ROLE_USER_USERNAME, TestSchema.ROLE_USER_PASSWORD_WRONG);

    Mockito.when(dao.getCredential(user)).thenReturn(TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.login(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void token_shouldReturnCorrectUser() throws Exception {
    Mockito.when(tokenService.parseToken(TestSchema.TOKEN_JWT_DTO_ROLE_USER))
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);

    AuthCredentialDTO actual = service.getUser(TestSchema.TOKEN_JWT_DTO_ROLE_USER);

    Assertions.assertEquals(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER, actual);
  }

  @Test
  void token_shouldReturnInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.getUser(token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void token_shouldReturnUnauthorized() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    Mockito.when(tokenService.parseToken(token))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.getUser(token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void getUsers_adminshouldReiceveListOfUsers() throws Exception {
    List<AuthCredentialEntity> listUsers =
        List.of(
            TestSchema.USER_CREDENTIAL_ENTITY_ROLE_ADMIN,
            TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER);
    Mockito.when(tokenService.parseToken(TestSchema.TOKEN_DTO_ROLE_ADMIN))
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO_ROLE_ADMIN);
    Mockito.when(dao.getUsers()).thenReturn(listUsers);

    List<AuthCredentialDTO> actuals = service.getUsers(TestSchema.TOKEN_DTO_ROLE_ADMIN);

    Assertions.assertEquals(listUsers.size(), actuals.size());
    for (int i = 0; i < actuals.size(); i++) {
      AuthCredentialDTO actual = actuals.get(i);
      AuthCredentialEntity expected = listUsers.get(i);

      Assertions.assertEquals(expected.getFirstName(), actual.getFirstName());
      Assertions.assertEquals(expected.getLastName(), actual.getLastName());
      Assertions.assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
      Assertions.assertEquals(expected.getEmail(), actual.getEmail());
      Assertions.assertEquals(expected.getUsername(), actual.getUsername());
      Assertions.assertEquals(expected.getRole(), actual.getRole());
    }
  }

  @Test
  void getUsers_adminShouldThrowInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);

    Mockito.when(tokenService.parseToken(token))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.getUsers(token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void getUsers_adminShouldThrowIfTheUserIsNotAnAdmin() throws Exception {
    Mockito.when(tokenService.parseToken(TestSchema.TOKEN_DTO_ROLE_ADMIN))
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class, () -> service.getUsers(TestSchema.TOKEN_DTO_ROLE_ADMIN));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }
}

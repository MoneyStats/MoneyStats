package com.moneystats.authentication;

import com.moneystats.authentication.DTO.*;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.ResponseMapping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SpringBootTest
public class AuthServiceTest {

  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @Mock private AuthCredentialDAO dao;
  @Mock TokenService tokenService;
  @Mock HttpServletRequest httpServletRequest;
  @InjectMocks private AuthCredentialService service;

  @Captor ArgumentCaptor<AuthCredentialInputDTO> authCredentialInputDTOArgumentCaptor;
  @Captor ArgumentCaptor<AuthCredentialDTO> authCredentialDTOArgumentCaptor;

  @BeforeEach
  void setPassword_setDefaultPassword() {
    TestSchema.USER_CREDENTIAL_DTO.setPassword(TestSchema.STRING_PASSWORD_ROLE_USER);
  }

  @Test
  void signUpUser_shouldInsertUserCorrectlyAndReturnUserAdded() throws Exception {
    Mockito.doNothing().when(dao).insertUserCredential(authCredentialDTOArgumentCaptor.capture());

    AuthResponseDTO response = service.signUp(TestSchema.USER_CREDENTIAL_DTO);

    Assertions.assertTrue(
        bCryptPasswordEncoder.matches(
            TestSchema.STRING_PASSWORD_ROLE_USER,
            authCredentialDTOArgumentCaptor.getValue().getPassword()));
    Assertions.assertEquals(
        TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER.getUsername(),
        authCredentialDTOArgumentCaptor.getValue().getUsername());
    Assertions.assertEquals(ResponseMapping.USER_ADDED_CORRECT, response.getMessage());
  }

  @Test
  void signUpUser_shouldReturnInvalidInput() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.STRING_USERNAME_ROLE_USER, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.signUp(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void signUpUser_shouldReturnUserPresent() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.STRING_USERNAME_ROLE_USER, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_PRESENT);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.signUp(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void signUpUser_shouldReturEmailPresent() throws Exception {
    AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.STRING_USERNAME_ROLE_USER, null);

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
    AuthCredentialInputDTO user =
        new AuthCredentialInputDTO(TestSchema.STRING_USERNAME_ROLE_USER, null);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_INPUT_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.login(user));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void login_shouldThrowWrongPassword() throws Exception {
    AuthCredentialInputDTO user =
        new AuthCredentialInputDTO(TestSchema.STRING_USERNAME_ROLE_USER, "my-wrong-password");

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
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO);

    AuthCredentialDTO actual = service.getUser(TestSchema.TOKEN_JWT_DTO_ROLE_USER);

    Assertions.assertEquals(TestSchema.USER_CREDENTIAL_DTO, actual);
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
    TokenDTO token = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);

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
        .thenReturn(TestSchema.USER_CREDENTIAL_DTO);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class, () -> service.getUsers(TestSchema.TOKEN_DTO_ROLE_ADMIN));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  /**
   * UPDATE USER TEST
   *
   * @throws Exception
   */
  @Test
  void update_shouldUpdateUserCorrectly() throws Exception {
    AuthResponseDTO expected = new AuthResponseDTO(ResponseMapping.USER_UPDATED);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTO = createValidAuthCredentialDTOToUpdate();
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(TestSchema.USER_CREDENTIAL_DTO);
    Mockito.doNothing().when(dao).updateUserById(authCredentialToUpdateDTO);

    AuthResponseDTO actualResponseDTO = service.updateUser(authCredentialToUpdateDTO, tokenDTO);

    Assertions.assertEquals(expected.getMessage(), actualResponseDTO.getMessage());
  }

  @Test
  void update_shouldThrowInvalidAuthCredentialDTOToUpdate() throws Exception {
    TokenDTO token = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialToUpdateDTO authCredentialToUpdateDTO = createValidAuthCredentialDTOToUpdate();
    authCredentialToUpdateDTO.setFirstName(null);
    authCredentialToUpdateDTO.setEmail(null);

    Mockito.when(tokenService.parseToken(token)).thenReturn(TestSchema.USER_CREDENTIAL_DTO);

    AuthenticationException expected =
        new AuthenticationException(
            AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_TO_UPDATE_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updateUser(authCredentialToUpdateDTO, token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void update_shouldThrowInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);
    AuthCredentialToUpdateDTO authCredentialToUpdateDTO = createValidAuthCredentialDTOToUpdate();

    Mockito.when(tokenService.parseToken(token))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updateUser(authCredentialToUpdateDTO, token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void update_shouldThrowUserNotMatch() throws Exception {
    TokenDTO token = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialToUpdateDTO authCredentialToUpdateDTO = createValidAuthCredentialDTOToUpdate();
    authCredentialToUpdateDTO.setUsername(null);

    Mockito.when(tokenService.parseToken(token)).thenReturn(TestSchema.USER_CREDENTIAL_DTO);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updateUser(authCredentialToUpdateDTO, token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  /**
   * Test updateUser()
   * @throws AuthenticationException
   */
  @Test
  void getUpdateUser_shouldReturnUpdateUser() throws AuthenticationException {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);

    AuthCredentialEntity authCredentialEntity = createValidAuthCredentialEntity();
    Mockito.when(dao.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);

    AuthCredentialDTO actual = service.getUpdateUser(tokenDTO);
    Assertions.assertEquals(authCredentialDTO.getFirstName(), actual.getFirstName());
    Assertions.assertEquals(authCredentialDTO.getLastName(), actual.getLastName());
    Assertions.assertEquals(authCredentialDTO.getDateOfBirth(), actual.getDateOfBirth());
    Assertions.assertEquals(authCredentialDTO.getEmail(), actual.getEmail());
    Assertions.assertEquals(authCredentialDTO.getUsername(), actual.getUsername());
  }

  @Test
  void getUpdateUser_shouldThrowUserNotMatch() throws Exception {
    TokenDTO token = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(token)).thenReturn(TestSchema.USER_CREDENTIAL_DTO);

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.getUpdateUser(token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void getUpdateUser_shouldThrowInvalidInput() throws Exception {
    TokenDTO token = new TokenDTO(null);

    Mockito.when(tokenService.parseToken(token))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(AuthenticationException.class, () -> service.getUpdateUser(token));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  /**
   * Test updatePassword
   *
   * @return
   */
  @Test
  void changePassword_shouldChangePasswordCorrectly() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    AuthResponseDTO expectedResponse = new AuthResponseDTO(ResponseMapping.PASSWORD_UPDATED);

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    AuthCredentialDTO actualAuthCredentialDTO = tokenService.parseToken(tokenDTO);

    Assertions.assertEquals(authCredentialDTO.getUsername(), actualAuthCredentialDTO.getUsername());
    Assertions.assertEquals(authCredentialDTO.getPassword(), actualAuthCredentialDTO.getPassword());
    Assertions.assertEquals(
        authCredentialDTO.getDateOfBirth(), actualAuthCredentialDTO.getDateOfBirth());
    Assertions.assertEquals(authCredentialDTO.getEmail(), actualAuthCredentialDTO.getEmail());

    AuthCredentialEntity authCredentialEntity = createValidAuthCredentialEntity();
    Mockito.when(dao.getCredential(authCredentialInputDTOArgumentCaptor.capture()))
        .thenReturn(authCredentialEntity);

    Assertions.assertEquals(authCredentialDTO.getUsername(), authCredentialEntity.getUsername());
    Assertions.assertEquals(
        authCredentialDTO.getDateOfBirth(), authCredentialEntity.getDateOfBirth());
    Assertions.assertEquals(authCredentialDTO.getEmail(), authCredentialEntity.getEmail());

    AuthResponseDTO actual = service.updatePassword(authChangePasswordInputDTO, tokenDTO);
    Assertions.assertEquals(expectedResponse.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnInvalidTokenDTO() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnInvalidAuthChangePasswordInput() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    authChangePasswordInputDTO.setUsername(null);
    authChangePasswordInputDTO.setOldPassword(null);

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(
            new AuthenticationException(
                AuthenticationException.Code.INVALID_AUTH_CHANGE_PASSWORD_INPUT_DTO));

    AuthenticationException expected =
        new AuthenticationException(
            AuthenticationException.Code.INVALID_AUTH_CHANGE_PASSWORD_INPUT_DTO);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnDiffertentPassword() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    authChangePasswordInputDTO.setConfirmNewPassword("different-password");

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.PASSWORD_NOT_MATCH));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.PASSWORD_NOT_MATCH);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnUserNotmatch() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    authChangePasswordInputDTO.setConfirmNewPassword("different-password");

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_NOT_MATCH);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnAuthCredentialEntityNotFound() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    authChangePasswordInputDTO.setConfirmNewPassword("different-password");

    Mockito.when(tokenService.parseToken(tokenDTO))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.USER_NOT_FOUND));

    AuthenticationException expected =
        new AuthenticationException(AuthenticationException.Code.USER_NOT_FOUND);
    AuthenticationException actual =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void changePassword_shouldThrowOnDiffertentPasswordOLD() throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    AuthChangePasswordInputDTO authChangePasswordInputDTO = createValidAuthChangePasswordInputDTO();
    authChangePasswordInputDTO.setOldPassword("different-password");

    Mockito.when(tokenService.parseToken(tokenDTO))
            .thenThrow(
                    new AuthenticationException(
                            AuthenticationException.Code.WRONG_CREDENTIAL));

    AuthenticationException expected =
            new AuthenticationException(
                    AuthenticationException.Code.WRONG_CREDENTIAL);
    AuthenticationException actual =
            Assertions.assertThrows(
                    AuthenticationException.class,
                    () -> service.updatePassword(authChangePasswordInputDTO, tokenDTO));

    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  // VALID INPUT

  private AuthChangePasswordInputDTO createValidAuthChangePasswordInputDTO() {
    return new AuthChangePasswordInputDTO(
        TestSchema.STRING_USERNAME_ROLE_USER,
        TestSchema.STRING_PASSWORD_ROLE_USER,
        TestSchema.STRING_PASSWORD_ROLE_USER,
        TestSchema.STRING_PASSWORD_ROLE_USER);
  }

  private AuthCredentialToUpdateDTO createValidAuthCredentialDTOToUpdate() {
    return new AuthCredentialToUpdateDTO(
        TestSchema.FIRSTNAME,
        TestSchema.LASTNAME,
        TestSchema.DATE_OF_BIRTH,
        TestSchema.EMAIL,
        TestSchema.STRING_USERNAME_ROLE_USER);
  }

  private AuthCredentialDTO createValidAuthCredentialDTO() {
    return new AuthCredentialDTO(
        TestSchema.FIRSTNAME,
        TestSchema.LASTNAME,
        TestSchema.DATE_OF_BIRTH,
        TestSchema.EMAIL,
        TestSchema.STRING_USERNAME_ROLE_USER,
        SecurityRoles.MONEYSTATS_USER_ROLE);
  }

  private AuthCredentialEntity createValidAuthCredentialEntity() {
    return new AuthCredentialEntity(
        TestSchema.FIRSTNAME,
        TestSchema.LASTNAME,
        TestSchema.DATE_OF_BIRTH,
        TestSchema.EMAIL,
        TestSchema.STRING_USERNAME_ROLE_USER,
        TestSchema.STRING_TOKEN_JWT_ROLE_USER,
        SecurityRoles.MONEYSTATS_USER_ROLE);
  }
}

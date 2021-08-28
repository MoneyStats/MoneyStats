package com.moneystats.authentication;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;

@SpringBootTest
public class AuthServiceTest {

	private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String DATE_OF_BIRTH = "dateOfBirth";
	private static final String EMAIL = "email";
	private static final String USER_USERNAME = "my-user-username";
	private static final String USER_PASSWORD = "my-user-password";
	private static final String USER_PASSWORD_WRONG = "the-user-wrong-password";
	private static final String USER_JWT = "the-user-jwt-token";
	private static final String USER_JWT_WRONG = "the-user-jwt-wrong";
	private static final String USER_PASS_HASHED = bCryptPasswordEncoder.encode(USER_PASSWORD);
	private static final TokenDTO USER_TOKEN = new TokenDTO(USER_JWT);
	private static final AuthCredentialInputDTO USER_USER_CREDENTIAL_DTO = new AuthCredentialInputDTO(USER_USERNAME,
			USER_PASSWORD);
	private static final AuthCredentialEntity USER_USER_CREDENTIAL_ENTITY = new AuthCredentialEntity(FIRSTNAME,
			LASTNAME, DATE_OF_BIRTH, EMAIL, USER_USERNAME, USER_PASS_HASHED, SecurityRoles.MONEYSTATS_USER_ROLE);
	private static final AuthCredentialDTO USER_USER_DTO = new AuthCredentialDTO(FIRSTNAME, LASTNAME, DATE_OF_BIRTH,
			EMAIL, USER_USERNAME, USER_PASSWORD, SecurityRoles.MONEYSTATS_USER_ROLE);

	private static final String ADMIN_USERNAME = "my-admin-username";
	private static final String ADMIN_PASSWORD = "my-admin-password";
	private static final String ADMIN_JWT = "the-admin-jwt-token";
	private static final String ADMIN_PASS_HASHED = bCryptPasswordEncoder.encode(ADMIN_PASSWORD);
	private static final AuthCredentialDTO ADMIN_USER = new AuthCredentialDTO(FIRSTNAME, LASTNAME, EMAIL,
			ADMIN_USERNAME, SecurityRoles.MONEYSTATS_ADMIN_ROLE);
	private static final TokenDTO ADMIN_TOKEN = new TokenDTO(ADMIN_JWT);
	private static final AuthCredentialEntity ADMIN_USER_CREDENTIAL_ENTITY = new AuthCredentialEntity(FIRSTNAME,
			LASTNAME, DATE_OF_BIRTH, EMAIL, ADMIN_USERNAME, ADMIN_PASS_HASHED, SecurityRoles.MONEYSTATS_ADMIN_ROLE);

	@Mock
	private AuthCredentialDAO dao;

	@InjectMocks
	private AuthCredentialService service;

	@Mock
	TokenService tokenService;

	@Captor
	ArgumentCaptor<AuthCredentialInputDTO> authCredentialInputDTOArgumentCaptor;

	@Captor
	ArgumentCaptor<TokenDTO> tokenDTOCaptor;

	@Captor
	ArgumentCaptor<AuthCredentialDTO> authCredentialDTOArgumentCaptor;

	@BeforeEach
	void setPassword_setDefaultPassword() {
		USER_USER_CREDENTIAL_DTO.setPassword(USER_PASSWORD);
	}

	@Test
	void signUpUser_shouldInsertUserCorrectly() throws Exception {
		Mockito.doNothing().when(dao).insertUserCredential(authCredentialDTOArgumentCaptor.capture());

		service.signUp(USER_USER_DTO);

		Assertions.assertTrue(
				bCryptPasswordEncoder.matches(USER_PASSWORD, authCredentialDTOArgumentCaptor.getValue().getPassword()));
		Assertions.assertEquals(USER_USER_CREDENTIAL_DTO.getUsername(),
				authCredentialDTOArgumentCaptor.getValue().getUsername());
	}

	@Test
	void signUpUser_shouldReturnInvalidInput() throws Exception {
		AuthCredentialDTO user = new AuthCredentialDTO(USER_USERNAME, null);

		AuthenticationException expected = new AuthenticationException(
				AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.signUp(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldReturnTokenCorrectly() throws Exception {
		Mockito.when(dao.getCredential(USER_USER_CREDENTIAL_DTO)).thenReturn(USER_USER_CREDENTIAL_ENTITY);
		Mockito.when(tokenService.generateToken(authCredentialDTOArgumentCaptor.capture())).thenReturn(USER_TOKEN);

		TokenDTO actual = service.login(USER_USER_CREDENTIAL_DTO);

		Assertions.assertEquals(USER_TOKEN.getAccessToken(), actual.getAccessToken());
	}


	@Test
	void login_shouldThrowWrongCredential() throws Exception {
		Mockito.when(dao.getCredential(USER_USER_CREDENTIAL_DTO)).thenReturn(null);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(USER_USER_CREDENTIAL_DTO));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldThrowInvalidInput() throws Exception {
		AuthCredentialInputDTO user = new AuthCredentialInputDTO(USER_USERNAME, null);

		AuthenticationException expected = new AuthenticationException(
				AuthenticationException.Code.INVALID_AUTH_INPUT_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldThrowWrongPassword() throws Exception {
		AuthCredentialInputDTO user = new AuthCredentialInputDTO(USER_USERNAME, USER_PASSWORD_WRONG);

		Mockito.when(dao.getCredential(user)).thenReturn(USER_USER_CREDENTIAL_ENTITY);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void token_shouldReturnCorrectUser() throws Exception {
		Mockito.when(tokenService.parseToken(USER_TOKEN)).thenReturn(USER_USER_DTO);

		AuthCredentialDTO actual = service.getUser(USER_TOKEN);

		Assertions.assertEquals(USER_USER_DTO.getUsername(), actual.getUsername());
	}

	@Test
	void token_shouldReturnInvalidInput() throws Exception {
		TokenDTO token = new TokenDTO(null);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUser(token));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void token_shouldReturnUnauthorized() throws Exception {
		TokenDTO token = new TokenDTO(USER_JWT_WRONG);

		Mockito.when(tokenService.parseToken(token))
				.thenThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED));

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUser(token));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void getUsers_adminshouldReiceveListOfUsers() throws Exception {
		List<AuthCredentialEntity> listUsers = List.of(ADMIN_USER_CREDENTIAL_ENTITY, USER_USER_CREDENTIAL_ENTITY);
		Mockito.when(tokenService.parseToken(ADMIN_TOKEN)).thenReturn(ADMIN_USER);
		Mockito.when(dao.getUsers()).thenReturn(listUsers);

		List<AuthCredentialDTO> actuals = service.getUsers(ADMIN_TOKEN);

		Assertions.assertEquals(listUsers.size(), actuals.size());
		for (int i = 0; i < actuals.size(); i++) {
			AuthCredentialDTO actual = actuals.get(i);
			AuthCredentialEntity expected = listUsers.get(i);

			Assertions.assertEquals(expected.getUsername(), actual.getUsername());
			Assertions.assertEquals(expected.getRole(), actual.getRole());
		}
	}

	@Test
	void getUsers_adminShouldThrowInvalidInput() throws Exception {
		TokenDTO token = new TokenDTO(null);

		Mockito.when(tokenService.parseToken(token))
				.thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUsers(token));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void getUsers_adminShouldThrowIfTheUserIsNotAnAdmin() throws Exception {
		Mockito.when(tokenService.parseToken(USER_TOKEN)).thenReturn(USER_USER_DTO);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUsers(USER_TOKEN));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

}

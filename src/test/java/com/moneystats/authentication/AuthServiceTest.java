package com.moneystats.authentication;

import java.util.List;

import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.SchemaDescription;
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
		TestSchema.USER_USER_DTO.setPassword(TestSchema.USER_PASSWORD);
	}

	@Test
	void signUpUser_shouldInsertUserCorrectlyAndReturnUserAdded() throws Exception {
		Mockito.doNothing().when(dao).insertUserCredential(authCredentialDTOArgumentCaptor.capture());

		AuthResponseDTO response = service.signUp(TestSchema.USER_USER_DTO);

		Assertions.assertTrue(
				bCryptPasswordEncoder.matches(TestSchema.USER_PASSWORD, authCredentialDTOArgumentCaptor.getValue().getPassword()));
		Assertions.assertEquals(TestSchema.USER_USER_CREDENTIAL_DTO.getUsername(),
				authCredentialDTOArgumentCaptor.getValue().getUsername());
		Assertions.assertEquals(SchemaDescription.USER_ADDED_CORRECT, response.getMessage());
	}

	@Test
	void signUpUser_shouldReturnInvalidInput() throws Exception {
		AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.USER_USERNAME, null);

		AuthenticationException expected = new AuthenticationException(
				AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.signUp(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void signUpUser_shouldReturnUserPresent() throws Exception {
		AuthCredentialDTO user = new AuthCredentialDTO(TestSchema.USER_USERNAME, null);

		AuthenticationException expected = new AuthenticationException(
				AuthenticationException.Code.USER_PRESENT);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.signUp(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldReturnTokenCorrectly() throws Exception {
		Mockito.when(dao.getCredential(TestSchema.USER_USER_CREDENTIAL_DTO)).thenReturn(TestSchema.USER_USER_CREDENTIAL_ENTITY);
		Mockito.when(tokenService.generateToken(authCredentialDTOArgumentCaptor.capture())).thenReturn(TestSchema.USER_TOKEN);

		TokenDTO actual = service.login(TestSchema.USER_USER_CREDENTIAL_DTO);

		Assertions.assertEquals(TestSchema.USER_TOKEN.getAccessToken(), actual.getAccessToken());
	}


	@Test
	void login_shouldThrowWrongCredential() throws Exception {
		Mockito.when(dao.getCredential(TestSchema.USER_USER_CREDENTIAL_DTO)).thenReturn(null);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(TestSchema.USER_USER_CREDENTIAL_DTO));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldThrowInvalidInput() throws Exception {
		AuthCredentialInputDTO user = new AuthCredentialInputDTO(TestSchema.USER_USERNAME, null);

		AuthenticationException expected = new AuthenticationException(
				AuthenticationException.Code.INVALID_AUTH_INPUT_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void login_shouldThrowWrongPassword() throws Exception {
		AuthCredentialInputDTO user = new AuthCredentialInputDTO(TestSchema.USER_USERNAME, TestSchema.USER_PASSWORD_WRONG);

		Mockito.when(dao.getCredential(user)).thenReturn(TestSchema.USER_USER_CREDENTIAL_ENTITY);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.login(user));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void token_shouldReturnCorrectUser() throws Exception {
		Mockito.when(tokenService.parseToken(TestSchema.USER_TOKEN)).thenReturn(TestSchema.USER_USER_DTO);

		AuthCredentialDTO actual = service.getUser(TestSchema.USER_TOKEN);

		Assertions.assertEquals(TestSchema.USER_USER_DTO, actual);
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
		TokenDTO token = new TokenDTO(TestSchema.USER_JWT_WRONG);

		Mockito.when(tokenService.parseToken(token))
				.thenThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED));

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUser(token));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void getUsers_adminshouldReiceveListOfUsers() throws Exception {
		List<AuthCredentialEntity> listUsers = List.of(TestSchema.ADMIN_USER_CREDENTIAL_ENTITY, TestSchema.USER_USER_CREDENTIAL_ENTITY);
		Mockito.when(tokenService.parseToken(TestSchema.ADMIN_TOKEN)).thenReturn(TestSchema.ADMIN_USER);
		Mockito.when(dao.getUsers()).thenReturn(listUsers);

		List<AuthCredentialDTO> actuals = service.getUsers(TestSchema.ADMIN_TOKEN);

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

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUsers(token));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}

	@Test
	void getUsers_adminShouldThrowIfTheUserIsNotAnAdmin() throws Exception {
		Mockito.when(tokenService.parseToken(TestSchema.USER_TOKEN)).thenReturn(TestSchema.USER_USER_DTO);

		AuthenticationException expected = new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED);
		AuthenticationException actual = Assertions.assertThrows(AuthenticationException.class,
				() -> service.getUsers(TestSchema.USER_TOKEN));

		Assertions.assertEquals(expected.getMessage(), actual.getMessage());
	}
}
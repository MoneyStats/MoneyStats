package com.moneystats.authentication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
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

	private static final String WRONG_PASSWORD = "giova";
	private static final String USER_USERNAME = "my-user-username";
	private static final String USER_PASSWORD = "my-user-password";
	private static final String USER_JWT = "my-user-jwt-token";
	private static final String USER_JWT_WRONG = "my-user-jwt-token-wrong";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String DATE_OF_BIRTH = "dateOfBirth";
	private static final String EMAIL = "email";
	private static final String USER_PASS_HASHED = bCryptPasswordEncoder.encode(USER_PASSWORD);

	private static final AuthCredentialInputDTO USER_USER_CREDENTIAL_DTO = new AuthCredentialInputDTO(USER_USERNAME,
			USER_PASSWORD);
	private static final AuthCredentialEntity USER_USER_CREDENTIAL_ENTITY = new AuthCredentialEntity(FIRSTNAME,
			LASTNAME, DATE_OF_BIRTH, EMAIL, USER_USERNAME, USER_PASS_HASHED, SecurityRoles.MONEYSTATS_USER_ROLE);
	private static final AuthCredentialDTO USER_USER_DTO = new AuthCredentialDTO(FIRSTNAME, LASTNAME, DATE_OF_BIRTH,
			EMAIL, USER_USERNAME, USER_PASSWORD, SecurityRoles.MONEYSTATS_USER_ROLE);
	private static final TokenDTO USER_TOKEN_JWT = new TokenDTO(USER_JWT);
	private static final AuthResponseDTO AUTH_RESPONSE = new AuthResponseDTO("USER_ADDED");

	@MockBean
	private AuthCredentialService credential;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void addUser_shouldReturnStatus200() throws Exception {
		String userAsString = objectMapper.writeValueAsString(USER_USER_DTO);

		Mockito.when(credential.signUp(USER_USER_DTO)).thenReturn(AUTH_RESPONSE);
		mockMvc.perform(post("/credential/signup").contentType(MediaType.APPLICATION_JSON).content(userAsString))
				.andExpect(status().isOk());
	}

	@Test
	void addUser_shouldReturnError400OnBadInput() throws Exception {
		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_CREDENTIAL_DTO))
				.when(credential).signUp(Mockito.any());
		mockMvc.perform(post("/credential/signup").contentType(MediaType.APPLICATION_JSON)
				.content("{\"user\": 30, \"password\": 90}")).andExpect(status().isBadRequest());
	}

	@Test
	void login_shouldReturnTokenCorrectly() throws Exception {
		String userAsString = objectMapper.writeValueAsString(USER_USER_CREDENTIAL_DTO);
		String tokenAsString = objectMapper.writeValueAsString(USER_TOKEN_JWT);

		Mockito.doReturn(USER_TOKEN_JWT).when(credential).login(Mockito.any());
		mockMvc.perform(post("/credential/login").contentType(MediaType.APPLICATION_JSON).content(userAsString))
				.andExpect(status().isOk()).andExpect(content().json(tokenAsString));
	}

	@Test
	void login_shouldReturnError400OnBadInput() throws Exception {
		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_AUTH_INPUT_DTO))
				.when(credential).login(Mockito.any());
		mockMvc.perform(post("/credential/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"user1\": \"giovanni\", \"password\": 90}")).andExpect(status().isBadRequest());
	}

	@Test
	void login_shouldReturnWrongcredential() throws Exception {
		AuthCredentialInputDTO user = new AuthCredentialInputDTO(USER_USERNAME, WRONG_PASSWORD);
		String userAsString = objectMapper.writeValueAsString(user);

		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL)).when(credential)
				.login(Mockito.any());
		mockMvc.perform(post("/credential/login").contentType(MediaType.APPLICATION_JSON).content(userAsString))
				.andExpect(status().is(401));
	}

	@Test
	void tokenUser_shouldReturnUser() throws Exception {
		String userAsString = objectMapper.writeValueAsString(USER_USER_DTO);

		Mockito.doReturn(USER_USER_DTO).when(credential).getUser(Mockito.any());
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/token").header("Authorization", "Bearer " + USER_JWT))
				.andExpect(status().isOk()).andExpect(content().json(userAsString));
	}

	@Test
	void tokenUser_shouldReturnUnauthorized() throws Exception {
		TokenDTO token = new TokenDTO(USER_JWT_WRONG);

		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED)).when(credential)
				.getUser(Mockito.any());
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/token").header("Authorization",
				"Bearer " + token.getAccessToken())).andExpect(status().is(401));
	}

	@Test
	void tokenUser_shouldReturnInvalidInput() throws Exception {
		TokenDTO token = new TokenDTO(null);

		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO)).when(credential)
				.getUser(token);
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/token")).andExpect(status().isBadRequest());
	}

	@Test
	void listUsers_adminShouldReceiveTheListCorrectly() throws Exception {
		List<AuthCredentialDTO> listUsers = List.of(USER_USER_DTO, USER_USER_DTO);
		String usersAsString = objectMapper.writeValueAsString(listUsers);

		Mockito.doReturn(listUsers).when(credential).getUsers(Mockito.any());
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/admin").header("Authorization", "Bearer " + USER_JWT))
				.andExpect(status().isOk()).andExpect(content().json(usersAsString));
	}

	@Test
	void listUsers_adminShouldReturnNotAllowed() throws Exception {
		TokenDTO token = new TokenDTO(USER_JWT_WRONG);

		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED)).when(credential)
				.getUsers(Mockito.any());
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/admin").header("Authorization",
				"Bearer " + token.getAccessToken())).andExpect(status().isUnauthorized());
	}

	@Test
	void listUsers_adminShouldReturnInvalidInput() throws Exception {
		TokenDTO token = new TokenDTO(null);

		Mockito.doThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO)).when(credential)
				.getUsers(token);
		mockMvc.perform(MockMvcRequestBuilders.get("/credential/admin")).andExpect(status().isBadRequest());
	}

}

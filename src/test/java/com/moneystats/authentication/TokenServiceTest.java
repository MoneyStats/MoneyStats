package com.moneystats.authentication;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class TokenServiceTest {

  @Value(value = "${jwt.secret}")
  private String secret;

  @Value(value = "${jwt.time}")
  private String expirationTime;

  @InjectMocks TokenService service;

  @BeforeEach
  void checkField() {
    ReflectionTestUtils.setField(service, "expirationTime", expirationTime);
    ReflectionTestUtils.setField(service, "secret", secret);
  }

  @Test
  void generateToken_parseToken_shouldReturnSameUser() throws Exception {
    AuthCredentialDTO userDto =
        new AuthCredentialDTO(
            TestSchema.FIRSTNAME,
            TestSchema.LASTNAME,
            TestSchema.DATE_OF_BIRTH,
            TestSchema.EMAIL,
            TestSchema.ROLE_USER_USERNAME,
            SecurityRoles.MONEYSTATS_USER_ROLE);

    TokenDTO token = service.generateToken(userDto);
    System.out.println(token.getAccessToken() + "Fine");
    AuthCredentialDTO parseUser = service.parseToken(token);

    Assertions.assertEquals(userDto.getFirstName(), parseUser.getFirstName());
    Assertions.assertEquals(userDto.getLastName(), parseUser.getLastName());
    Assertions.assertEquals(userDto.getDateOfBirth(), parseUser.getDateOfBirth());
    Assertions.assertEquals(userDto.getEmail(), parseUser.getEmail());
    Assertions.assertEquals(userDto.getUsername(), parseUser.getUsername());
    Assertions.assertEquals(userDto.getRole(), parseUser.getRole());
  }

  @Test
  void parseToken_shouldThrowUnauthorizedOnInvalidToken() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
    AuthenticationException actualException =
        Assertions.assertThrows(AuthenticationException.class, () -> service.parseToken(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }
}

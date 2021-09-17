package com.moneystats.MoneyStats.webTest;

import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.MoneyStats.web.WebException;
import com.moneystats.MoneyStats.web.WebService;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class WebServiceTest {

    @Mock private AuthCredentialDAO authCredentialDAO;
    @InjectMocks private WebService webService;
    @InjectMocks private TokenService service;

    @Value(value = "${jwt.secret}")
    private String secret;
    @Value(value = "${jwt.time}")
    private String expirationTime;


    @BeforeEach
    void checkField() {
        ReflectionTestUtils.setField(service, "expirationTime", expirationTime);
        ReflectionTestUtils.setField(webService, "secret", secret);
    }

    /**
     * Test to checkLogin User
     * @throws Exception
     */
    @Test
    void test_checkUser_shouldReturnCurrentUserLogged() throws WebException {
        AuthCredentialDTO expected =
                new AuthCredentialDTO(
                        TestSchema.FIRSTNAME,
                        TestSchema.LASTNAME,
                        TestSchema.DATE_OF_BIRTH,
                        TestSchema.EMAIL,
                        TestSchema.ROLE_USER_USERNAME,
                        SecurityRoles.MONEYSTATS_USER_ROLE);

        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

        AuthCredentialDTO actual = webService.checkUser(tokenDTO);
        Assertions.assertEquals(expected.getFirstName(), actual.getFirstName());
        Assertions.assertEquals(expected.getLastName(), actual.getLastName());
        Assertions.assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getUsername(), actual.getUsername());
        Assertions.assertEquals(expected.getRole(), actual.getRole());
    }

    @Test
    void test_checkUser_shouldThrowsOnLoginRequired() throws Exception {
        TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

        WebException expectedException =
                new WebException(WebException.Code.LOGIN_REQUIRED);
        WebException actualException =
                Assertions.assertThrows(WebException.class, () -> webService.checkUser(token));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }
}

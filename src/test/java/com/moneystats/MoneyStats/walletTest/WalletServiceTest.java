package com.moneystats.MoneyStats.walletTest;

import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.WalletService;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.SchemaDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class WalletServiceTest {

  @Mock private IWalletDAO walletDAO;
  @InjectMocks private WalletService walletService;

  @Value(value = "${jwt.secret}")
  private String secret;

  @Value(value = "${jwt.time}")
  private String expirationTime;

  @InjectMocks private TokenService tokenService;

  @BeforeEach
  void checkField() {
    ReflectionTestUtils.setField(tokenService, "expirationTime", expirationTime);
    ReflectionTestUtils.setField(tokenService, "secret", secret);
  }

  @Test
  void test_walletDTOlist_shouldReturnTheList() throws Exception {
    List<WalletDTO> walletDTO = DTOTestObjets.walletDTOS;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;
    TokenDTO token = tokenService.generateToken(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);
    AuthCredentialDTO parseUser = tokenService.parseToken(token);

    Mockito.when(walletDAO.findAll()).thenReturn(walletEntities);

    Mockito.when(walletService.getAll(token)).thenReturn(walletDTO);

    Mockito.when(tokenService.parseToken(token)).thenReturn(parseUser);

    List<WalletDTO> actual = walletService.getAll(token);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertEquals(walletDTO.get(i), actual.get(i));
    }
  }

  @Test
  void test_walletDTOList_shouldThrowsOnInvalidTokenDTO() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    AuthenticationException expectedException =
            new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
    AuthenticationException actualException =
            Assertions.assertThrows(AuthenticationException.class, () -> tokenService.parseToken(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_walletDTOList_shouldThrowsOnEmptyTokenDTO() throws Exception {
    TokenDTO token = new TokenDTO("");

    AuthenticationException expectedException =
            new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
            Assertions.assertThrows(AuthenticationException.class, () -> walletService.getAll(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_walletDTOList_shouldThrowsOnWalletNotFound() throws Exception {
    TokenDTO tokenDTO = tokenService.generateToken(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);
    List<WalletEntity> list = new ArrayList<>();

    Mockito.when(walletDAO.findAll()).thenReturn(list);

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(TestSchema.USER_CREDENTIAL_DTO_ROLE_USER);

    WalletException expectedException =
            new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    WalletException actualException =
            Assertions.assertThrows(WalletException.class, () -> walletService.getAll(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldAddCorrectly() throws Exception {
    WalletResponseDTO expected = new WalletResponseDTO(SchemaDescription.USER_ADDED_CORRECT);

  }
}


package com.moneystats.MoneyStats.walletTest;

import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletResponseDTO;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.commStats.wallet.WalletService;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.source.DTOTestObjets;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.entity.AuthCredentialEntity;
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
import java.util.Optional;

@SpringBootTest
public class WalletServiceTest {

  @Mock private IWalletDAO walletDAO;
  @Mock private IStatementDAO statementDAO;
  @Mock private ICategoryDAO categoryDAO;
  @Mock private AuthCredentialDAO authCredentialDAO;
  @InjectMocks private WalletService walletService;

  @Value(value = "${jwt.secret}")
  private String secret;

  @Value(value = "${jwt.time}")
  private String expirationTime;

  @Mock private TokenService tokenService;

  @BeforeEach
  void checkField() {
    ReflectionTestUtils.setField(tokenService, "expirationTime", expirationTime);
    ReflectionTestUtils.setField(tokenService, "secret", secret);
  }

  @Test
  void test_walletDTOlist_shouldReturnTheList() throws Exception {
    List<WalletDTO> walletDTO = DTOTestObjets.walletDTOS;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    AuthCredentialInputDTO authCredentialInputDTO =
        new AuthCredentialInputDTO(authCredentialDTO.getUsername(), authCredentialDTO.getRole());
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTO))
        .thenReturn(authCredentialEntity);
    Mockito.when(walletDAO.findAllByUserId(authCredentialEntity.getId()))
        .thenReturn(walletEntities);

    List<WalletDTO> actual = walletService.getAll(tokenDTO);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertEquals(walletDTO.get(i), actual.get(i));
    }
  }

  @Test
  void test_walletDTOList_shouldThrowsTokenDTORequired() throws Exception {
    TokenDTO token = new TokenDTO("");

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
        Assertions.assertThrows(AuthenticationException.class, () -> walletService.getAll(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_walletDTOList_shouldThrowsOnInvalidTokenDTO() throws Exception {
    TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);

    Mockito.when(tokenService.parseToken(token))
        .thenThrow(new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> tokenService.parseToken(token));

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
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    List<WalletEntity> list = new ArrayList<>();
    Long idUser = 1L;

    Mockito.when(walletDAO.findAllByUserId(idUser)).thenReturn(list);

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);

    WalletException expectedException = new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(WalletException.class, () -> walletService.getAll(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldAddCorrectly() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletResponseDTO expected = new WalletResponseDTO(SchemaDescription.USER_ADDED_CORRECT);
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    WalletEntity walletEntity = DTOTestObjets.walletEntities.get(0);
    Integer idCategory = 1;

    Mockito.when(walletDAO.save(walletEntity)).thenReturn(walletEntity);

    Mockito.when(walletService.addWalletEntity(tokenDTO, idCategory, walletDTO))
        .thenReturn(expected);

    WalletResponseDTO actual = walletService.addWalletEntity(tokenDTO, idCategory, walletDTO);
    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void test_addWallet_shouldThrowsOnInvalidWallet() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    walletDTO.setName(null);
    Integer idCategory = 1;

    WalletException expectedException =
        new WalletException(WalletException.Code.INVALID_WALLET_DTO);
    WalletException actualException =
        Assertions.assertThrows(
            WalletException.class,
            () -> walletService.addWalletEntity(tokenDTO, idCategory, walletDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldThrowsTokenDTORequired() throws Exception {
    TokenDTO token = new TokenDTO("");
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = 1;

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class,
            () -> walletService.addWalletEntity(token, idCategory, walletDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldThrowsOnCategoryNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = 1;

    Mockito.when(authCredentialDAO.getCredential(TestSchema.USER_CREDENTIAL_INPUT_DTO_ROLE_USER))
        .thenReturn(TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER);
    Mockito.when(categoryDAO.findById(idCategory)).thenReturn(null);

    WalletException expectedException =
        new WalletException(WalletException.Code.CATEGORY_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(
            WalletException.class,
            () -> walletService.addWalletEntity(tokenDTO, idCategory, walletDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_deleteWallet_shouldDeleteWallet() throws Exception {
    WalletResponseDTO expected = new WalletResponseDTO(SchemaDescription.WALLET_DELETE_CORRECT);
    Optional<WalletEntity> walletEntity = Optional.ofNullable(DTOTestObjets.walletEntities.get(0));
    List<StatementEntity> statementEntity = DTOTestObjets.statementEntityList;
    Long idWallet = 1L;

    Mockito.when(walletDAO.findById(idWallet)).thenReturn(walletEntity);

    Mockito.when(statementDAO.findStatementByWalletId(idWallet)).thenReturn(statementEntity);

    Mockito.doNothing().when(walletDAO).deleteById(idWallet);

    WalletResponseDTO actual = walletService.deleteWalletEntity(idWallet);
    Assertions.assertEquals(expected.getMessage(), actual.getMessage());
  }

  @Test
  void test_deleteWallet_shouldThrowsOnWalletNotFound() throws Exception {
    Long idWallet = 1L;

    WalletException expectedException = new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(
            WalletException.class, () -> walletService.deleteWalletEntity(idWallet));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_deleteWallet_shouldThrowsOnStatementNotFound() throws Exception {
    Long idWallet = 1L;
    Optional<WalletEntity> walletEntity = Optional.ofNullable(DTOTestObjets.walletEntities.get(0));

    Mockito.when(walletDAO.findById(idWallet)).thenReturn(walletEntity);

    WalletException expectedException =
        new WalletException(WalletException.Code.STATEMENT_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(
            WalletException.class, () -> walletService.deleteWalletEntity(idWallet));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }
}
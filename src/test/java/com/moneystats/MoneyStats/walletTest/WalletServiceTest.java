package com.moneystats.MoneyStats.walletTest;

import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
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
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import com.moneystats.generic.SchemaDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

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

  @Mock private TokenService tokenService;

  @Test
  void test_walletDTOlist_shouldReturnTheList() throws Exception {
    List<WalletDTO> walletDTO = DTOTestObjets.walletDTOS;
    List<WalletEntity> walletEntities = DTOTestObjets.walletEntities;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(walletDAO.findAllByUserId(Mockito.any())).thenReturn(walletEntities);

    List<WalletEntity> actual = walletService.getAll(tokenDTO);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertEquals(walletDTO.get(i).getName(), actual.get(i).getName());
      Assertions.assertEquals(walletDTO.get(i).getUser(), actual.get(i).getUser());
      Assertions.assertEquals(
          walletDTO.get(i).getCategoryEntity().getId(), actual.get(i).getCategory().getId());
      Assertions.assertEquals(
          walletDTO.get(i).getCategoryEntity().getName(),
          actual.get(i).getCategory().getName());
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
        .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expectedException =
        new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actualException =
        Assertions.assertThrows(
            AuthenticationException.class, () -> tokenService.parseToken(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }



  @Test
  void test_walletDTOList_shouldThrowsOnWalletNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    List<WalletEntity> list = new ArrayList<>();

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(walletDAO.findAllByUserId(Mockito.any())).thenReturn(list);

    WalletException expectedException = new WalletException(WalletException.Code.WALLET_NOT_FOUND);
    WalletException actualException =
        Assertions.assertThrows(WalletException.class, () -> walletService.getAll(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldAddCorrectly() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletResponseDTO expected = new WalletResponseDTO(SchemaDescription.WALLET_ADDED_CORRECT);
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
    Optional<CategoryEntity> categoryEntity = Optional.ofNullable(DTOTestObjets.categoryEntity);
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    WalletEntity walletEntity = DTOTestObjets.walletEntities.get(0);
    Integer idCategory = 1;

    Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);
    Mockito.when(categoryDAO.findById(Mockito.any())).thenReturn(categoryEntity);
    Mockito.when(walletDAO.save(walletEntity)).thenReturn(walletEntity);

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
  void test_addWallet_shouldThrowsOnEmptyTokenDTO() throws Exception {
    TokenDTO token = new TokenDTO("");

    Mockito.when(tokenService.parseToken(token))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED));

    AuthenticationException expectedException =
            new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
    AuthenticationException actualException =
            Assertions.assertThrows(AuthenticationException.class, () -> tokenService.parseToken(token));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldThrowsOnInvalidTokenDTO() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = 1;

    Mockito.when(tokenService.parseToken(tokenDTO))
            .thenThrow(new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO));

    AuthenticationException expectedException =
            new AuthenticationException(AuthenticationException.Code.INVALID_TOKEN_DTO);
    AuthenticationException actualException =
            Assertions.assertThrows(AuthenticationException.class, () -> tokenService.parseToken(tokenDTO));

    Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
  }

  @Test
  void test_addWallet_shouldThrowsOnCategoryNotFound() throws Exception {
    TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
    WalletDTO walletDTO = DTOTestObjets.walletDTO;
    Integer idCategory = null;
    AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
    AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.getCredential(Mockito.any())).thenReturn(authCredentialEntity);

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

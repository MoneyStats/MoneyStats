package com.moneystats.MoneyStats.statementTest;

import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementResponseDTO;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.statement.StatementService;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.DTO.WalletDTO;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class StatementServiceTest {

    @Mock
    private IWalletDAO walletDAO;
    @Mock private IStatementDAO statementDAO;
    @Mock private ICategoryDAO categoryDAO;
    @Mock private AuthCredentialDAO authCredentialDAO;
    @InjectMocks
    private StatementService statementService;
    @Mock private TokenService tokenService;

    @Test
    void test_addStatement_shouldAdd() throws Exception {
        StatementDTO statementDTO = DTOTestObjets.statementDTO;
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        StatementEntity statementEntity = DTOTestObjets.statementEntityList.get(0);
        StatementResponseDTO expected = new StatementResponseDTO(SchemaDescription.STATEMENT_ADDED_CORRECT);
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        Optional<WalletEntity> walletEntity = Optional.ofNullable(DTOTestObjets.walletEntities.get(0));

        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(walletDAO.findById(Mockito.any())).thenReturn(walletEntity);
        Mockito.when(statementDAO.save(Mockito.any()))
                .thenReturn(statementEntity);

        StatementResponseDTO actual = statementService.addStatement(tokenDTO, statementDTO);
        Assertions.assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void test_addStatement_shouldThrowsTokenDTORequired() throws Exception {
        TokenDTO token = new TokenDTO("");
        StatementDTO statementDTO = DTOTestObjets.statementDTO;

        AuthenticationException expectedException =
                new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
        AuthenticationException actualException =
                Assertions.assertThrows(AuthenticationException.class, () -> statementService.addStatement(token, statementDTO));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_addStatement_shouldThrowsOnInvalidTokenDTO() throws Exception {
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
    void test_addStatement_shouldBeMappedOnUserNotFound() throws Exception {
        StatementDTO statementDTO = DTOTestObjets.statementDTO;
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        StatementEntity statementEntity = DTOTestObjets.statementEntityList.get(0);
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialInputDTO authCredentialInputDTO =
                new AuthCredentialInputDTO(authCredentialDTO.getUsername(), authCredentialDTO.getRole());
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

        Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTO))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
        Mockito.when(statementDAO.save(statementEntity))
                .thenReturn(statementEntity);

        StatementException expectedException =
                new StatementException(StatementException.Code.USER_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.addStatement(tokenDTO, statementDTO));
        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_addStatement_shouldThrowsOnWalletNotFound() throws Exception {
        StatementDTO statementDTO = DTOTestObjets.statementDTO;
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        StatementEntity statementEntity = DTOTestObjets.statementEntityList.get(0);
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(statementDAO.save(statementEntity))
                .thenReturn(statementEntity);

        StatementException expectedException =
                new StatementException(StatementException.Code.WALLET_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.addStatement(tokenDTO, statementDTO));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listOfDate_shouldReturnList() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        List<String> listDateExpected = List.of("2021-06-09");

        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(statementDAO.selectdistinctstatement(Mockito.any())).thenReturn(listDateExpected);

        List<String> actual = statementService.listOfDate(tokenDTO);
        Assertions.assertEquals(listDateExpected, actual);
    }

    @Test
    void test_listOfDate_shouldThrowsTokenDTORequired() throws Exception {
        TokenDTO token = new TokenDTO("");

        AuthenticationException expectedException =
                new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
        AuthenticationException actualException =
                Assertions.assertThrows(AuthenticationException.class, () -> statementService.listOfDate(token));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listOfDate_shouldThrowsOnInvalidTokenDTO() throws Exception {
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
    void test_listOfDate_shouldBeMappedOnUserNotFound() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        StatementEntity statementEntity = DTOTestObjets.statementEntityList.get(0);
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialInputDTO authCredentialInputDTO =
                new AuthCredentialInputDTO(authCredentialDTO.getUsername(), authCredentialDTO.getRole());
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

        Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTO))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);

        StatementException expectedException =
                new StatementException(StatementException.Code.USER_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.listOfDate(tokenDTO));
        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listOfDate_shouldThrowsOnListNotFound() throws Exception {
        StatementDTO statementDTO = DTOTestObjets.statementDTO;
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        List<String> listDate = new ArrayList<>();

        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(statementDAO.selectdistinctstatement(Mockito.any()))
                .thenReturn(listDate);

        StatementException expectedException =
                new StatementException(StatementException.Code.WALLET_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.addStatement(tokenDTO, statementDTO));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listStatementByDate_shouldReturnList() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        List<StatementEntity> listStatementExpected = DTOTestObjets.statementEntityList;
        String date = "2021-06-09";

        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(statementDAO.findAllByUserIdAndDateOrderByWalletId(Mockito.any(), Mockito.any())).thenReturn(listStatementExpected);

        List<StatementEntity> actual = statementService.listStatementByDate(tokenDTO, date);
        Assertions.assertEquals(listStatementExpected, actual);
    }

    @Test
    void test_listStatementByDate_shouldThrowsTokenDTORequired() throws Exception {
        TokenDTO token = new TokenDTO("");
        String date = "2021-06-09";

        AuthenticationException expectedException =
                new AuthenticationException(AuthenticationException.Code.TOKEN_REQUIRED);
        AuthenticationException actualException =
                Assertions.assertThrows(AuthenticationException.class, () -> statementService.listStatementByDate(token, date));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listStatementByDate_shouldThrowsOnInvalidTokenDTO() throws Exception {
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
    void test_listStatementByDate_shouldBeMappedOnUserNotFound() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialInputDTO authCredentialInputDTO =
                new AuthCredentialInputDTO(authCredentialDTO.getUsername(), authCredentialDTO.getRole());
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        String date = "2021-06-09";

        Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTO))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);

        StatementException expectedException =
                new StatementException(StatementException.Code.USER_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.listStatementByDate(tokenDTO, date));
        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

    @Test
    void test_listStatementByDate_shouldThrowsOnListNotFound() throws Exception {
        TokenDTO tokenDTO = TestSchema.TOKEN_JWT_DTO_ROLE_USER;
        AuthCredentialDTO authCredentialDTO = TestSchema.USER_CREDENTIAL_DTO_ROLE_USER;
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;
        List<StatementEntity> listStatementDate = new ArrayList<>();
        String date = "2021-06-09";

        Mockito.when(authCredentialDAO.getCredential(Mockito.any()))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(Mockito.any())).thenReturn(authCredentialDTO);
        Mockito.when(statementDAO.findAllByUserIdAndDateOrderByWalletId(Mockito.any(), Mockito.any()))
                .thenReturn(listStatementDate);

        StatementException expectedException =
                new StatementException(StatementException.Code.STATEMENT_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.listStatementByDate(tokenDTO, date));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }
}

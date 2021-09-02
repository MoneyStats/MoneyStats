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

import java.util.List;

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
        AuthCredentialInputDTO authCredentialInputDTO =
                new AuthCredentialInputDTO(authCredentialDTO.getUsername(), authCredentialDTO.getRole());
        AuthCredentialEntity authCredentialEntity = TestSchema.USER_CREDENTIAL_ENTITY_ROLE_USER;

        Mockito.when(authCredentialDAO.getCredential(authCredentialInputDTO))
                .thenReturn(authCredentialEntity);
        Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
        Mockito.when(statementDAO.save(statementEntity))
                .thenReturn(statementEntity);

        StatementResponseDTO actual = statementService.addStatement(tokenDTO, statementDTO);
            Assertions.assertEquals(expected, actual);
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
        TokenDTO token = new TokenDTO(TestSchema.ROLE_USER_TOKEN_JWT_WRONG);
        StatementDTO statementDTO = DTOTestObjets.statementDTO;

        Mockito.when(walletDAO.findById(statementDTO.getWalletEntity().getId()))
                .thenThrow(new StatementException(StatementException.Code.WALLET_NOT_FOUND));

        StatementException expectedException =
                new StatementException(StatementException.Code.WALLET_NOT_FOUND);
        StatementException actualException =
                Assertions.assertThrows(
                        StatementException.class, () -> statementService.addStatement(token, statementDTO));

        Assertions.assertEquals(expectedException.getCode(), actualException.getCode());
    }

}

package com.moneystats.MoneyStats.databaseExportTest;

import com.moneystats.MoneyStats.commStats.category.ICategoryDAO;
import com.moneystats.MoneyStats.commStats.category.entity.CategoryEntity;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseResponseDTO;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseException;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseRepository;
import com.moneystats.MoneyStats.databaseImportExport.DatabaseService;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateService;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.TokenService;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.authentication.utils.TestSchema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DatabaseServiceTest {

  @Mock private IWalletDAO walletDAO;
  @Mock private IStatementDAO statementDAO;
  @Mock private ICategoryDAO categoryDAO;
  @Mock private AuthCredentialDAO authCredentialDAO;
  @InjectMocks private DatabaseService databaseService;
  @Mock private TokenService tokenService;
  @Mock private TemplateService templateService;

  @Test
  void testBackupDatabase() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    StatementEntity statementEntity = createValidStatementEntity();
    List<StatementEntity> statementEntityList = List.of(statementEntity);
    DatabaseResponseDTO expected = new DatabaseResponseDTO(DatabaseResponseDTO.String.EXPORTED);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<AuthCredentialEntity> authCredentialEntities = List.of(authCredentialEntity);
    WalletEntity walletEntity = createValidStatementEntity().getWallet();
    List<WalletEntity> walletEntities = List.of(walletEntity);
    DatabaseCommandDTO databaseCommandDTO = createValidDatabaseCommandDTO();
    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(statementDAO.findAll()).thenReturn(statementEntityList);
    Mockito.when(walletDAO.findAll()).thenReturn(walletEntities);
    Mockito.when(authCredentialDAO.getAllUsers()).thenReturn(authCredentialEntities);

    TemplateDTO templateDTO = createValidTemplateDTO();
    Mockito.when(templateService.getTemplate(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE))
        .thenReturn(templateDTO);
    Mockito.when(templateService.applyTemplate(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(List.of("my-list"));
    Mockito.doNothing().when(templateService).saveTemplate(Mockito.any(), Mockito.anyList());
    Mockito.doNothing().when(templateService).applyAndSaveJsonBackup(Mockito.any(), Mockito.any());

    DatabaseResponseDTO actual = databaseService.backupDatabase(databaseCommandDTO, tokenDTO);
    Assertions.assertEquals(expected.getResponse(), actual.getResponse());
  }

  @Test
  void testBackupDatabase_shouldThowOnInvalidCommand() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    StatementEntity statementEntity = createValidStatementEntity();
    List<StatementEntity> statementEntityList = List.of(statementEntity);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    List<AuthCredentialEntity> authCredentialEntities = List.of(authCredentialEntity);
    WalletEntity walletEntity = createValidStatementEntity().getWallet();
    List<WalletEntity> walletEntities = List.of(walletEntity);
    DatabaseCommandDTO databaseCommandDTO = createValidDatabaseCommandDTO();
    databaseCommandDTO.setDatabase(DatabaseCommand.IMPORT_DUMP_COMMAND);

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(statementDAO.findAll()).thenReturn(statementEntityList);
    Mockito.when(walletDAO.findAll()).thenReturn(walletEntities);
    Mockito.when(authCredentialDAO.getAllUsers()).thenReturn(authCredentialEntities);

    DatabaseException expectedExpeption =
        new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);

    DatabaseException actualException =
        Assertions.assertThrows(
            DatabaseException.class,
            () -> databaseService.backupDatabase(databaseCommandDTO, tokenDTO));

    Assertions.assertEquals(expectedExpeption.getCode(), actualException.getCode());
  }

  /*@Test
  void testRestoreDatabase() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    StatementEntity statementEntity = createValidStatementEntity();
    List<StatementEntity> statementEntityList = List.of(statementEntity);
    DatabaseResponseDTO expected = new DatabaseResponseDTO(DatabaseResponseDTO.String.IMPORTED);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    WalletEntity walletEntity = createValidStatementEntity().getWallet();
    List<WalletEntity> walletEntities = List.of(walletEntity);
    DatabaseCommandDTO databaseCommandDTO = createValidDatabaseCommandDTO();
    databaseCommandDTO.setDatabase(DatabaseCommand.IMPORT_DUMP_COMMAND);
    databaseCommandDTO.setFilePath("src/test/resources/restoreTest/testBackupFile.backup");

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.deleteAllUser())
        .thenReturn(new AuthResponseDTO(AuthResponseDTO.String.DELETED));
    Mockito.doNothing().when(authCredentialDAO).insertUser(authCredentialEntity);
    Mockito.when(statementDAO.saveAll(statementEntityList)).thenReturn(statementEntityList);
    Mockito.when(walletDAO.saveAll(walletEntities)).thenReturn(walletEntities);

    databaseService.databaseRepository = Mockito.mock(DatabaseRepository.class);
    Mockito.doNothing()
        .when(databaseService.databaseRepository)
        .executingResetCounterScript(TemplatePlaceholders.FILEPATH_RESET_COUNTER);

    DatabaseResponseDTO actual = databaseService.restoreDatabase(databaseCommandDTO, tokenDTO);
    Assertions.assertEquals(expected.getResponse(), actual.getResponse());
  }*/

  @Test
  void testRestoreDatabase_shouldThrowOnInvalidCommand() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    StatementEntity statementEntity = createValidStatementEntity();
    List<StatementEntity> statementEntityList = List.of(statementEntity);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    WalletEntity walletEntity = createValidStatementEntity().getWallet();
    List<WalletEntity> walletEntities = List.of(walletEntity);
    DatabaseCommandDTO databaseCommandDTO = createValidDatabaseCommandDTO();
    databaseCommandDTO.setDatabase(DatabaseCommand.EXPORT_DUMP_COMMAND);
    databaseCommandDTO.setFilePath("src/test/resources/restoreTest/testBackupFile.backup");

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.deleteAllUser())
        .thenReturn(new AuthResponseDTO(AuthResponseDTO.String.DELETED));
    Mockito.doNothing().when(authCredentialDAO).insertUser(authCredentialEntity);
    Mockito.when(statementDAO.saveAllAndFlush(statementEntityList)).thenReturn(statementEntityList);
    Mockito.when(walletDAO.saveAllAndFlush(walletEntities)).thenReturn(walletEntities);

    DatabaseException expectedExpeption =
        new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);

    DatabaseException actualException =
        Assertions.assertThrows(
            DatabaseException.class,
            () -> databaseService.restoreDatabase(databaseCommandDTO, tokenDTO));

    Assertions.assertEquals(expectedExpeption.getCode(), actualException.getCode());
  }

  @Test
  void testRestoreDatabase_shouldThrowOnInvalidPath() throws Exception {
    TokenDTO tokenDTO = new TokenDTO(TestSchema.STRING_TOKEN_JWT_ROLE_USER);
    StatementEntity statementEntity = createValidStatementEntity();
    List<StatementEntity> statementEntityList = List.of(statementEntity);
    AuthCredentialDTO authCredentialDTO = createValidAuthCredentialDTO();
    AuthCredentialEntity authCredentialEntity = createValidStatementEntity().getUser();
    WalletEntity walletEntity = createValidStatementEntity().getWallet();
    List<WalletEntity> walletEntities = List.of(walletEntity);
    DatabaseCommandDTO databaseCommandDTO = createValidDatabaseCommandDTO();
    databaseCommandDTO.setDatabase(DatabaseCommand.IMPORT_DUMP_COMMAND);
    databaseCommandDTO.setFilePath("src/test/resources/restoreTest/testBackup");

    Mockito.when(tokenService.parseToken(tokenDTO)).thenReturn(authCredentialDTO);
    Mockito.when(authCredentialDAO.deleteAllUser())
        .thenReturn(new AuthResponseDTO(AuthResponseDTO.String.DELETED));
    Mockito.doNothing().when(authCredentialDAO).insertUser(authCredentialEntity);
    Mockito.when(statementDAO.saveAllAndFlush(statementEntityList)).thenReturn(statementEntityList);
    Mockito.when(walletDAO.saveAllAndFlush(walletEntities)).thenReturn(walletEntities);

    DatabaseException expectedExpeption =
        new DatabaseException(DatabaseException.Code.ERROR_ON_IMPORT_DATABASE);

    DatabaseException actualException =
        Assertions.assertThrows(
            DatabaseException.class,
            () -> databaseService.restoreDatabase(databaseCommandDTO, tokenDTO));

    Assertions.assertEquals(expectedExpeption.getCode(), actualException.getCode());
  }

  private TemplateDTO createValidTemplateDTO() {
    return new TemplateDTO(
        List.of("my content"), Map.of("my-key", List.of("my.values")), new File("test.sql"));
  }

  private DatabaseCommandDTO createValidDatabaseCommandDTO() {
    return new DatabaseCommandDTO(
        TemplatePlaceholders.FILEPATH_BACKUP,
        DatabaseCommand.EXPORT_DUMP_COMMAND,
        SecurityRoles.MONEYSTATS_ADMIN_ROLE);
  }

  private StatementEntity createValidStatementEntity() {
    AuthCredentialEntity authCredentialEntity =
        new AuthCredentialEntity(
            1L,
            TestSchema.FIRSTNAME,
            TestSchema.LASTNAME,
            TestSchema.DATE_OF_BIRTH,
            TestSchema.EMAIL,
            TestSchema.STRING_USERNAME_ROLE_USER,
            TestSchema.STRING_TOKEN_JWT_ROLE_USER,
            SecurityRoles.MONEYSTATS_USER_ROLE);
    CategoryEntity categoryEntity = new CategoryEntity(1, "Category-name");
    WalletEntity walletEntity =
        new WalletEntity(1L, "my-Wallet-name", categoryEntity, authCredentialEntity, null);
    return new StatementEntity("01-01-2021", 10.00D, authCredentialEntity, walletEntity);
  }

  private AuthCredentialDTO createValidAuthCredentialDTO() {
    return new AuthCredentialDTO(
        TestSchema.FIRSTNAME,
        TestSchema.LASTNAME,
        TestSchema.DATE_OF_BIRTH,
        TestSchema.EMAIL,
        TestSchema.STRING_USERNAME_ROLE_USER,
        TestSchema.STRING_TOKEN_JWT_ROLE_USER,
        SecurityRoles.MONEYSTATS_USER_ROLE);
  }
}

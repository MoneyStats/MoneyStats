package com.moneystats.MoneyStats.databaseImportExport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseJSONExportDTO;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseResponseDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.DTO.TemplateDTO;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateException;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplatePlaceholders;
import com.moneystats.MoneyStats.databaseImportExport.template.TemplateService;
import com.moneystats.authentication.AuthCredentialDAO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.TokenValidation;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Service
public class DatabaseService {

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseService.class);
  @Autowired TemplateService templateService;
  @Autowired IStatementDAO statementDAO;
  @Autowired IWalletDAO walletDAO;
  @Autowired AuthCredentialDAO authCredentialDAO;
  @Autowired public DatabaseRepository databaseRepository;

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Value(value = "${database.name}")
  private String databaseName;

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public DatabaseResponseDTO backupDatabase(
      DatabaseCommandDTO databaseCommandDTO, TokenDTO tokenDTO)
      throws AuthenticationException, DatabaseException, TemplateException {
    databaseCommandDTO.setFilePath(TemplatePlaceholders.FILEPATH_BACKUP);
    DatabaseValidator.validateDatabaseCommandDTO(databaseCommandDTO);
    TokenValidation.validateTokenDTO(tokenDTO);
    Map<String, List<String>> placeholder = new HashMap<>();
    DatabaseJSONExportDTO placeStatement = placeholderStatement();
    DatabaseJSONExportDTO placeWallet = placeholderWallet();
    DatabaseJSONExportDTO placeUsers = placeholderUsers();
    placeholder.putAll(placeStatement.getPlaceholder());
    placeholder.putAll(placeWallet.getPlaceholder());
    placeholder.putAll(placeUsers.getPlaceholder());

    Map<String, String> placeholderMustache = new HashMap<>();
    placeholderMustache.put(TemplatePlaceholders.DATABASE_PLACEHOLDER, databaseName);
    placeholderMustache.put(TemplatePlaceholders.DATE_PLACEHOLDER, LocalDate.now().toString());
    DatabaseResponseDTO response = new DatabaseResponseDTO();

    if (!(databaseCommandDTO.getDatabase() == DatabaseCommand.EXPORT_DUMP_COMMAND)) {
      LOG.error("Wrong database command, Command: {}", databaseCommandDTO.getDatabase());
      throw new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);
    }
    TemplateDTO getExportTemplate =
        templateService.getTemplate(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    List<String> appliedTemplate =
        templateService.applyTemplate(getExportTemplate, placeholder, placeholderMustache);
    templateService.saveTemplate(databaseCommandDTO.getFilePath(), appliedTemplate);

    // Export JSON File
    DatabaseJSONExportDTO databaseJSONToExport =
        new DatabaseJSONExportDTO(
            placeStatement.getStatementEntities(),
            placeWallet.getWalletEntities(),
            placeUsers.getAuthCredentialEntities());
    templateService.applyAndSaveJsonBackup(databaseJSONToExport, databaseCommandDTO.getFilePath());
    response.setResponse(DatabaseResponseDTO.String.EXPORTED);
    return response;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public DatabaseResponseDTO restoreDatabase(
      DatabaseCommandDTO databaseCommandDTO, TokenDTO tokenDTO)
      throws AuthenticationException, DatabaseException {
    DatabaseValidator.validateDatabaseCommandDTO(databaseCommandDTO);
    TokenValidation.validateTokenDTO(tokenDTO);
    DatabaseResponseDTO response = new DatabaseResponseDTO();
    if (!(databaseCommandDTO.getDatabase() == DatabaseCommand.IMPORT_DUMP_COMMAND)) {
      LOG.error("Wrong database command, Command: {}", databaseCommandDTO.getDatabase());
      throw new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);
    }
    String folderFilePath =
        TemplatePlaceholders.FILEPATH_BACKUP
            + databaseCommandDTO.getFilePath()
            + "/json_dump_backup_"
            + databaseCommandDTO.getFilePath()
            + ".backup";
    File sqlExportTemplate = new File(folderFilePath);

    Scanner scanner;
    try {
      scanner = new Scanner(sqlExportTemplate);
    } catch (FileNotFoundException e) {
      LOG.error("Backup File not found {},", DatabaseException.Code.ERROR_ON_IMPORT_DATABASE);
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_IMPORT_DATABASE);
    }
    StringBuilder databaseJsonString = new StringBuilder();
    while (scanner.hasNextLine()) {
      databaseJsonString.append(scanner.nextLine());
    }
    scanner.close();
    DatabaseJSONExportDTO databaseJSONExportDTO;
    try {
      databaseJSONExportDTO =
          objectMapper.readValue(databaseJsonString.toString(), DatabaseJSONExportDTO.class);
    } catch (JsonProcessingException e) {
      LOG.error(
          "A Problem occurred during deserialize object {},",
          DatabaseException.Code.ERROR_ON_IMPORT_DATABASE);
      throw new DatabaseException(DatabaseException.Code.ERROR_ON_IMPORT_DATABASE);
    }
    deleteAndInsertIntoDatabase(databaseJSONExportDTO);

    response.setResponse(DatabaseResponseDTO.String.IMPORTED);
    LOG.info("Database successfully imported, PATH: {}", databaseCommandDTO.getFilePath());
    return response;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public List<String> getFolderDatabase() throws DatabaseException {
    File folder = new File(TemplatePlaceholders.FILEPATH_BACKUP);
    File[] listFolder = folder.listFiles();
    if (listFolder == null) {
      LOG.error("List Folder is Null");
      throw new DatabaseException(DatabaseException.Code.FILE_NOT_FOUND);
    }
    List<String> folderList = new ArrayList<>();
    LOG.info("Adding directory to get");
    for (int i = listFolder.length - 1; i >= 0; i--) {
      if (listFolder[i].isFile()) {
        LOG.error("File is not a Directory {}", listFolder[i].getName());
        throw new DatabaseException(DatabaseException.Code.NOT_A_DIRECTORY);
      }
      folderList.add(listFolder[i].getName());
      LOG.info("Added to list {}", listFolder[i].getName());
    }
    return folderList;
  }

  private void deleteAndInsertIntoDatabase(DatabaseJSONExportDTO databaseJSONExportDTO)
      throws AuthenticationException, DatabaseException {
    LOG.info("Deleting Database data");
    statementDAO.deleteAll();
    walletDAO.deleteAll();
    authCredentialDAO.deleteAllUser();
    LOG.info("Deleting completed");
    LOG.info("Insert Users");
    for (AuthCredentialEntity user : databaseJSONExportDTO.getAuthCredentialEntities()) {
      authCredentialDAO.insertUser(user);
    }
    LOG.info("Users insert Successfully");
    String filePath = TemplatePlaceholders.FILEPATH_RESET_COUNTER;
    databaseRepository.executingResetCounterScript(filePath);

    LOG.info("Insert Wallets");
    walletDAO.saveAll(databaseJSONExportDTO.getWalletEntities());
    LOG.info("Wallets insert successfully");
    LOG.info("Insert Statements");
    statementDAO.saveAll(databaseJSONExportDTO.getStatementEntities());
    LOG.info("Statements insert successfully");
  }

  private DatabaseJSONExportDTO placeholderStatement() {
    Map<String, List<String>> placeholder = new HashMap<>();
    List<StatementEntity> exportStatementEntity = statementDAO.findAll();
    List<String> compositStatementString = new ArrayList<>();
    for (int i = 0; i < exportStatementEntity.size(); i++) {
      String databaseLine =
          "("
              + exportStatementEntity.get(i).getId()
              + ",'"
              + exportStatementEntity.get(i).getDate()
              + "',"
              + exportStatementEntity.get(i).getValue()
              + ","
              + exportStatementEntity.get(i).getUser().getId()
              + ","
              + exportStatementEntity.get(i).getWallet().getId()
              + ")";
      if (exportStatementEntity.size() == i + 1) {
        databaseLine += ";";
      }
      if (exportStatementEntity.size() != i + 1) {
        databaseLine += "," + TemplatePlaceholders.FIX_TEXT;
      }
      compositStatementString.add(databaseLine);
    }
    placeholder.put(TemplatePlaceholders.STATEMENT_PLACEHOLDER, compositStatementString);
    return new DatabaseJSONExportDTO(placeholder, exportStatementEntity, null, null);
  }

  private DatabaseJSONExportDTO placeholderWallet() {
    Map<String, List<String>> placeholder = new HashMap<>();
    List<WalletEntity> exportWalletEntity = walletDAO.findAll();
    List<String> compositWalletString = new ArrayList<>();
    for (int i = 0; i < exportWalletEntity.size(); i++) {
      String databaseLine =
          "("
              + exportWalletEntity.get(i).getId()
              + ",'"
              + exportWalletEntity.get(i).getName()
              + "', "
              + exportWalletEntity.get(i).getCategory().getId()
              + ", "
              + exportWalletEntity.get(i).getUser().getId()
              + ")";
      if (exportWalletEntity.size() == i + 1) {
        databaseLine += ";";
      }
      if (exportWalletEntity.size() != i + 1) {
        databaseLine += "," + TemplatePlaceholders.FIX_TEXT;
      }
      compositWalletString.add(databaseLine);
    }
    placeholder.put(TemplatePlaceholders.WALLET_PLACEHOLDER, compositWalletString);
    return new DatabaseJSONExportDTO(placeholder, null, exportWalletEntity, null);
  }

  private DatabaseJSONExportDTO placeholderUsers() throws AuthenticationException {
    Map<String, List<String>> placeholder = new HashMap<>();
    List<AuthCredentialEntity> exportUsersEntity = authCredentialDAO.getAllUsers();
    List<String> compositStatementString = new ArrayList<>();
    for (int i = 0; i < exportUsersEntity.size(); i++) {
      String databaseLine =
          "("
              + exportUsersEntity.get(i).getId()
              + ",'"
              + exportUsersEntity.get(i).getDateOfBirth()
              + "', '"
              + exportUsersEntity.get(i).getEmail()
              + "', '"
              + exportUsersEntity.get(i).getFirstName()
              + "', '"
              + exportUsersEntity.get(i).getLastName()
              + "', '"
              + exportUsersEntity.get(i).getPassword()
              + "', '"
              + exportUsersEntity.get(i).getRole()
              + "', '"
              + exportUsersEntity.get(i).getUsername()
              + "')";
      if (exportUsersEntity.size() == i + 1) {
        databaseLine += ";";
      }
      if (exportUsersEntity.size() != i + 1) {
        databaseLine += "," + TemplatePlaceholders.FIX_TEXT;
      }
      compositStatementString.add(databaseLine);
    }
    placeholder.put(TemplatePlaceholders.USERS_PLACEHOLDER, compositStatementString);
    return new DatabaseJSONExportDTO(placeholder, null, null, exportUsersEntity);
  }
}

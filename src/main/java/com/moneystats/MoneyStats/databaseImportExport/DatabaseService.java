package com.moneystats.MoneyStats.databaseImportExport;

import com.moneystats.MoneyStats.commStats.statement.IStatementDAO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.IWalletDAO;
import com.moneystats.MoneyStats.commStats.wallet.entity.WalletEntity;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommand;
import com.moneystats.MoneyStats.databaseImportExport.DTO.DatabaseCommandDTO;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseService.class);
  @Autowired TemplateService templateService;
  @Autowired IStatementDAO statementDAO;
  @Autowired IWalletDAO walletDAO;
  @Autowired AuthCredentialDAO authCredentialDAO;

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public DatabaseResponseDTO backupDatabase(
      DatabaseCommandDTO databaseCommandDTO, TokenDTO tokenDTO)
      throws AuthenticationException, DatabaseException, TemplateException {
    DatabaseValidator.validateDatabaseCommandDTO(databaseCommandDTO);
    TokenValidation.validateTokenDTO(tokenDTO);
    Map<String, List<String>> placeholder = new HashMap<>();
    placeholder.putAll(placeholderStatement());
    placeholder.putAll(placeholderWallet());
    placeholder.putAll(placeholderUsers());

    Map<String, String> placeholderMustache = new HashMap<>();
    placeholderMustache.put(TemplatePlaceholders.DATABASE_PLACEHOLDER, "moneystats");
    placeholderMustache.put(TemplatePlaceholders.DATE_PLACEHOLDER, LocalDate.now().toString());
    DatabaseResponseDTO response = new DatabaseResponseDTO();

    if (!(databaseCommandDTO.getDatabase() == DatabaseCommand.EXPORT_DUMP_COMMAND)) {
      LOG.error("Wrong database command, Command: {}", databaseCommandDTO.getDatabase());
      throw new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);
    }
    TemplateDTO getExportTemplate =
            templateService.getTemplate(TemplatePlaceholders.GET_EXPORT_DATABASE_TEMPLATE);
    List<String> appliedTemplate = templateService.applyTemplate(getExportTemplate, placeholder, placeholderMustache);
    templateService.saveTemplate(databaseCommandDTO.getFilePath(), appliedTemplate);
    response.setResponse(DatabaseResponseDTO.String.EXPORTED);
    return response;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public DatabaseResponseDTO restoreDatabase(
      DatabaseCommandDTO databaseCommandDTO, TokenDTO tokenDTO)
      throws AuthenticationException, DatabaseException, TemplateException {
    DatabaseValidator.validateDatabaseCommandDTO(databaseCommandDTO);
    TokenValidation.validateTokenDTO(tokenDTO);
    DatabaseResponseDTO response = new DatabaseResponseDTO();
    if (!(databaseCommandDTO.getDatabase() == DatabaseCommand.IMPORT_DUMP_COMMAND)) {
      LOG.error("Wrong database command, Command: {}", databaseCommandDTO.getDatabase());
      throw new DatabaseException(DatabaseException.Code.INVALID_DATABASE_COMMAND_DTO);
    }
    response.setResponse(DatabaseResponseDTO.String.IMPORTED);
    return response;
    }

  private Map<String, List<String>> placeholderStatement() {
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
    return placeholder;
  }

  private Map<String, List<String>> placeholderWallet() {
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
    return placeholder;
  }

  private Map<String, List<String>> placeholderUsers() throws AuthenticationException {
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
    return placeholder;
  }
}

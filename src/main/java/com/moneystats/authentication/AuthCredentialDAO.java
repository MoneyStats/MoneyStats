package com.moneystats.authentication;

import com.moneystats.authentication.AuthenticationException.Code;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthCredentialToUpdateDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.timeTracker.LogTimeTracker;
import com.moneystats.generic.timeTracker.LoggerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthCredentialDAO {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private String dbAddress;
  private String username;
  private String password;

  public AuthCredentialDAO(
      @Value("${spring.datasource.url}") String dbAddress,
      @Value("${spring.datasource.username}") String username,
      @Value("${spring.datasource.password}") String password) {
    super();
    this.dbAddress = dbAddress;
    this.username = username;
    this.password = password;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public void insertUserCredential(AuthCredentialDTO user) throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.INSERT_INTO_USERS;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, user.getFirstName());
      pstm.setString(2, user.getLastName());
      pstm.setString(3, user.getDateOfBirth());
      pstm.setString(4, user.getEmail());
      pstm.setString(5, user.getUsername());
      pstm.setString(6, user.getPassword());
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Database Error in InsertUserCredential");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public AuthCredentialEntity getCredential(AuthCredentialInputDTO user)
      throws AuthenticationException {
    AuthCredentialEntity userCredential = null;
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.SELECT_FROM_USERS;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, user.getUsername());
      ResultSet rs = pstm.executeQuery();
      if (rs.next()) {
        userCredential =
            new AuthCredentialEntity(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("date_of_birth"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"));
      }
    } catch (SQLException e) {
      LOG.error("Database Error in getCredential");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
    return userCredential;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public AuthCredentialEntity findByEmail(String email) throws AuthenticationException {
    AuthCredentialEntity userCredential = null;
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.FIND_BY_EMAIL;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, email);
      ResultSet rs = pstm.executeQuery();
      if (rs.next()) {
        userCredential =
            new AuthCredentialEntity(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("date_of_birth"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"));
      }
    } catch (SQLException e) {
      LOG.error("Database Error in getCredential");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
    return userCredential;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public List<AuthCredentialEntity> getUsers() throws AuthenticationException {
    List<AuthCredentialEntity> listUser = new ArrayList<AuthCredentialEntity>();
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.SELECT_FROM_USERS_WHERE_ROLE;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      ResultSet rs = pstm.executeQuery();

      while (rs.next()) {
        listUser.add(
            new AuthCredentialEntity(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("date_of_birth"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")));
      }
    } catch (SQLException e) {
      LOG.error("Database Error in getAllUser");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
    return listUser;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public void updateUserById(AuthCredentialToUpdateDTO authCredentialToUpdateDTO)
      throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.UPDATE_USERS;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, authCredentialToUpdateDTO.getFirstName());
      pstm.setString(2, authCredentialToUpdateDTO.getLastName());
      pstm.setString(3, authCredentialToUpdateDTO.getDateOfBirth());
      pstm.setString(4, authCredentialToUpdateDTO.getEmail());
      pstm.setString(5, authCredentialToUpdateDTO.getUsername());
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Process aborted during update {}", Code.DATABASE_ERROR);
      throw new AuthenticationException(Code.DATABASE_ERROR);
    }
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public void updatePasswordUserById(String usernameDB, String passwordDB)
      throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.UPDATE_PASSWORD;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, passwordDB);
      pstm.setString(2, usernameDB);
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Process aborted during update {}", Code.DATABASE_ERROR);
      throw new AuthenticationException(Code.DATABASE_ERROR);
    }
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public List<AuthCredentialEntity> getAllUsers() throws AuthenticationException {
    List<AuthCredentialEntity> listUser = new ArrayList<AuthCredentialEntity>();
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.FIND_ALL;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      ResultSet rs = pstm.executeQuery();

      while (rs.next()) {
        listUser.add(
                new AuthCredentialEntity(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("date_of_birth"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")));
      }
    } catch (SQLException e) {
      LOG.error("Database Error in getAllUser");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
    return listUser;
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public AuthResponseDTO deleteAllUser() throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = AuthCredentialEntity.DELETE_ALL_USERS;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Database Error in deleteAllUsers");
      throw new AuthenticationException(AuthenticationException.Code.DATABASE_ERROR);
    }
    return new AuthResponseDTO(AuthResponseDTO.String.DELETED);
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public void insertUser(AuthCredentialEntity authCredentialEntity)
          throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand =
          AuthCredentialEntity.INSERT_USER_FROM_BACKUP;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setLong(1, authCredentialEntity.getId());
      pstm.setString(2, authCredentialEntity.getFirstName());
      pstm.setString(3, authCredentialEntity.getLastName());
      pstm.setString(4, authCredentialEntity.getDateOfBirth());
      pstm.setString(5, authCredentialEntity.getEmail());
      pstm.setString(6, authCredentialEntity.getUsername());
      pstm.setString(7, authCredentialEntity.getPassword());
      pstm.setString(8, authCredentialEntity.getRole());
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Process aborted during insert {}", Code.DATABASE_ERROR);
      throw new AuthenticationException(Code.DATABASE_ERROR);
    }
  }
}

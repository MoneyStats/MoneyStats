package com.moneystats.authentication;

import com.moneystats.authentication.AuthenticationException.Code;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthCredentialToUpdateDTO;
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

  private static final String UPDATE_USERS =
      "UPDATE users SET first_name = ?, last_name = ?, date_of_birth = ?, email = ? WHERE username = ?";

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE username = ?";
  private static final String SELECT_FROM_USERS_WHERE_ROLE =
      "SELECT * FROM users WHERE role = 'USER'";
  private static final String SELECT_FROM_USERS = "SELECT * FROM users WHERE username = ?";
  private static final String FIND_ALL = "SELECT * FROM users";
  private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
  private static final String INSERT_INTO_USERS =
      "INSERT INTO users (first_name, last_name, date_of_birth, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?, 'USER')";

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
      String sqlCommand = INSERT_INTO_USERS;
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
      String sqlCommand = SELECT_FROM_USERS;
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
      String sqlCommand = FIND_BY_EMAIL;
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
      String sqlCommand = SELECT_FROM_USERS_WHERE_ROLE;
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
      String sqlCommand = UPDATE_USERS;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, authCredentialToUpdateDTO.getFirstName());
      pstm.setString(2, authCredentialToUpdateDTO.getLastName());
      pstm.setString(3, authCredentialToUpdateDTO.getDateOfBirth());
      pstm.setString(4, authCredentialToUpdateDTO.getEmail());
      pstm.setString(5, authCredentialToUpdateDTO.getUsername());
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Process aborted during update {}", e);
      throw new AuthenticationException(Code.DATABASE_ERROR);
    }
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public void updatePasswordUserById(String usernameDB, String passwordDB)
      throws AuthenticationException {
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = UPDATE_PASSWORD;
      PreparedStatement pstm = connection.prepareStatement(sqlCommand);
      pstm.setString(1, passwordDB);
      pstm.setString(2, usernameDB);
      pstm.execute();
    } catch (SQLException e) {
      LOG.error("Process aborted during update {}", e);
      throw new AuthenticationException(Code.DATABASE_ERROR);
    }
  }

  @LoggerMethod(type = LogTimeTracker.ActionType.APP_DATABASE_ENDPOINT)
  public List<AuthCredentialEntity> getAllUsers() throws AuthenticationException {
    List<AuthCredentialEntity> listUser = new ArrayList<AuthCredentialEntity>();
    try {
      Connection connection = DriverManager.getConnection(dbAddress, username, password);
      String sqlCommand = FIND_ALL;
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
}

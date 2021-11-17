package com.moneystats.authentication.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class AuthCredentialEntity {

  public static final String UPDATE_USERS =
          "UPDATE users SET first_name = ?, last_name = ?, date_of_birth = ?, email = ? WHERE username = ?";

  public static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE username = ?";
  public static final String SELECT_FROM_USERS_WHERE_ROLE =
          "SELECT * FROM users WHERE role = 'USER'";
  public static final String SELECT_FROM_USERS = "SELECT * FROM users WHERE username = ?";
  public static final String FIND_ALL = "SELECT * FROM users";
  public static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
  public static final String INSERT_INTO_USERS =
          "INSERT INTO users (first_name, last_name, date_of_birth, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?, 'USER')";
public static final String DELETE_ALL_USERS = "delete from users";
public static final String INSERT_USER_FROM_BACKUP = "insert into users(id, first_name, last_name, date_of_birth, email, username, password, role) values (?, ?, ?, ?, ?, ?, ?, ?)";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "date_of_birth", nullable = false)
  private String dateOfBirth;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "role", nullable = false)
  private String role;

  public AuthCredentialEntity(
      Long id,
      String firstName,
      String lastName,
      String dateOfBirth,
      String email,
      String username,
      String password,
      String role) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.email = email;
    this.username = username;
    this.password = password;
    this.role = role;
  }

  public AuthCredentialEntity(
      String firstName,
      String lastName,
      String dateOfBirth,
      String email,
      String username,
      String password,
      String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.email = email;
    this.username = username;
    this.password = password;
    this.role = role;
  }

  public AuthCredentialEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}

package com.moneystats.authentication.utils;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.SchemaDescription;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestSchema {
  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  public static final String WRONG_PASSWORD = "giova";
  public static final String USER_USERNAME = "my-user-username";
  public static final String USER_USERNAME_WRONG = "my-user-username-wrong";
  public static final String USER_PASSWORD = "my-user-password";
  public static final String USER_PASSWORD_WRONG = "my-user-password-wrong";
  public static final String USER_JWT = "my-user-jwt-token";
  public static final String USER_JWT_WRONG = "my-user-jwt-token-wrong";
  public static final String FIRSTNAME = "firstName";
  public static final String LASTNAME = "lastName";
  public static final String DATE_OF_BIRTH = "dateOfBirth";
  public static final String EMAIL = "email";
  public static final String USER_PASS_HASHED = bCryptPasswordEncoder.encode(USER_PASSWORD);

  public static final AuthCredentialInputDTO USER_USER_CREDENTIAL_DTO =
      new AuthCredentialInputDTO(USER_USERNAME, USER_PASSWORD);
  public static final AuthCredentialEntity USER_USER_CREDENTIAL_ENTITY =
      new AuthCredentialEntity(
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
          USER_USERNAME,
          USER_PASS_HASHED,
          SecurityRoles.MONEYSTATS_USER_ROLE);
  public static final AuthCredentialDTO USER_USER_DTO =
      new AuthCredentialDTO(
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
          USER_USERNAME,
          USER_PASSWORD,
          SecurityRoles.MONEYSTATS_USER_ROLE);
  public static final TokenDTO USER_TOKEN_JWT = new TokenDTO(USER_JWT);

  public static final TokenDTO USER_TOKEN = new TokenDTO(USER_JWT);

  public static final String ADMIN_USERNAME = "my-admin-username";
  public static final String ADMIN_PASSWORD = "my-admin-password";
  public static final String ADMIN_JWT = "the-admin-jwt-token";
  public static final String ADMIN_PASS_HASHED = bCryptPasswordEncoder.encode(ADMIN_PASSWORD);
  public static final AuthCredentialDTO ADMIN_USER = new AuthCredentialDTO(FIRSTNAME, LASTNAME, EMAIL,
          ADMIN_USERNAME, SecurityRoles.MONEYSTATS_ADMIN_ROLE);
  public static final TokenDTO ADMIN_TOKEN = new TokenDTO(ADMIN_JWT);
  public static final AuthCredentialEntity ADMIN_USER_CREDENTIAL_ENTITY = new AuthCredentialEntity(FIRSTNAME,
          LASTNAME, DATE_OF_BIRTH, EMAIL, ADMIN_USERNAME, ADMIN_PASS_HASHED, SecurityRoles.MONEYSTATS_ADMIN_ROLE);

  public static final AuthResponseDTO AUTH_RESPONSE_DTO = new AuthResponseDTO(SchemaDescription.USER_ADDED_CORRECT);
}

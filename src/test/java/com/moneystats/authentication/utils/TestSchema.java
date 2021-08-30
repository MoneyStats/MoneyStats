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
  public static final String ROLE_USER_USERNAME = "my-user-username";
  public static final String ROLE_USER_USERNAME_WRONG = "my-user-username-wrong";
  public static final String ROLE_USER_PASSWORD = "my-user-password";
  public static final String ROLE_USER_PASSWORD_WRONG = "my-user-password-wrong";
  public static final String ROLE_USER_TOKEN_JWT = "my-user-jwt-token";
  public static final String ROLE_USER_TOKEN_JWT_WRONG = "my-user-jwt-token-wrong";
  public static final String FIRSTNAME = "firstName";
  public static final String LASTNAME = "lastName";
  public static final String DATE_OF_BIRTH = "dateOfBirth";
  public static final String EMAIL = "email";
  public static final String ROLE_USER_PASS_HASHED = bCryptPasswordEncoder.encode(ROLE_USER_PASSWORD);

  public static final AuthCredentialInputDTO USER_CREDENTIAL_INPUT_DTO_ROLE_USER =
      new AuthCredentialInputDTO(ROLE_USER_USERNAME, ROLE_USER_PASSWORD);
  public static final AuthCredentialEntity USER_CREDENTIAL_ENTITY_ROLE_USER =
      new AuthCredentialEntity(
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
              ROLE_USER_USERNAME,
              ROLE_USER_PASS_HASHED,
          SecurityRoles.MONEYSTATS_USER_ROLE);
  public static final AuthCredentialDTO USER_CREDENTIAL_DTO_ROLE_USER =
      new AuthCredentialDTO(
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
              ROLE_USER_USERNAME,
              ROLE_USER_PASSWORD,
          SecurityRoles.MONEYSTATS_USER_ROLE);
  public static final TokenDTO TOKEN_JWT_DTO_ROLE_USER = new TokenDTO(ROLE_USER_TOKEN_JWT);

  public static final String ROLE_ADMIN_USERNAME = "my-admin-username";
  public static final String ROLE_ADMIN_PASSWORD = "my-admin-password";
  public static final String ROLE_ADMIN_TOKEN_JWT = "the-admin-jwt-token";
  public static final String ROLE_ADMIN_PASSWORD_HASHED = bCryptPasswordEncoder.encode(ROLE_ADMIN_PASSWORD);
  public static final AuthCredentialDTO USER_CREDENTIAL_DTO_ROLE_ADMIN =
      new AuthCredentialDTO(
          FIRSTNAME, LASTNAME, EMAIL, ROLE_ADMIN_USERNAME, SecurityRoles.MONEYSTATS_ADMIN_ROLE);
  public static final TokenDTO TOKEN_DTO_ROLE_ADMIN = new TokenDTO(ROLE_ADMIN_TOKEN_JWT);
  public static final AuthCredentialEntity USER_CREDENTIAL_ENTITY_ROLE_ADMIN =
      new AuthCredentialEntity(
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
              ROLE_ADMIN_USERNAME,
              ROLE_ADMIN_PASSWORD_HASHED,
          SecurityRoles.MONEYSTATS_ADMIN_ROLE);

  public static final AuthResponseDTO AUTH_RESPONSE_DTO =
      new AuthResponseDTO(SchemaDescription.USER_ADDED_CORRECT);
}

package com.moneystats.authentication.utils;

import com.moneystats.authentication.DTO.*;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.ResponseMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestSchema {
  public static final String FIRSTNAME = "firstName";
  public static final String LASTNAME = "lastName";
  public static final String DATE_OF_BIRTH = "dateOfBirth";
  public static final String EMAIL = "email";
  private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  public static final String STRING_USERNAME_ROLE_USER = "my-user-username";
  public static final String STRING_PASSWORD_ROLE_USER = "my-user-password";
  public static final String WRONG_PASSWORD = "giova";
  public static final String STRING_TOKEN_JWT_ROLE_USER = bCryptPasswordEncoder.encode(STRING_PASSWORD_ROLE_USER);
  public static final String ROLE_USER_TOKEN_JWT_WRONG = "my-user-jwt-token-wrong";

  public static final AuthCredentialDTO USER_CREDENTIAL_DTO =
          new AuthCredentialDTO(
                  FIRSTNAME,
                  LASTNAME,
                  DATE_OF_BIRTH,
                  EMAIL,
                  STRING_USERNAME_ROLE_USER,
                  STRING_PASSWORD_ROLE_USER,
                  SecurityRoles.MONEYSTATS_USER_ROLE);

  // Has To Be verified

  public static final AuthCredentialInputDTO USER_CREDENTIAL_INPUT_DTO_ROLE_USER =
      new AuthCredentialInputDTO(STRING_USERNAME_ROLE_USER, STRING_PASSWORD_ROLE_USER);

  public static final AuthCredentialEntity USER_CREDENTIAL_ENTITY_ROLE_USER =
      new AuthCredentialEntity(
          1L,
          FIRSTNAME,
          LASTNAME,
          DATE_OF_BIRTH,
          EMAIL,
              STRING_USERNAME_ROLE_USER,
          STRING_TOKEN_JWT_ROLE_USER,
          SecurityRoles.MONEYSTATS_USER_ROLE);



  public static final TokenDTO TOKEN_JWT_DTO_ROLE_USER = new TokenDTO(STRING_TOKEN_JWT_ROLE_USER);

  public static final String ROLE_ADMIN_USERNAME = "my-admin-username";
  public static final String ROLE_ADMIN_PASSWORD = "my-admin-password";
  public static final String ROLE_ADMIN_TOKEN_JWT = "the-admin-jwt-token";
  public static final String ROLE_ADMIN_PASSWORD_HASHED =
      bCryptPasswordEncoder.encode(ROLE_ADMIN_PASSWORD);
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
      new AuthResponseDTO(ResponseMapping.USER_ADDED_CORRECT);

  public static final AuthCredentialToUpdateDTO AUTH_CREDENTIAL_TO_UPDATE_DTO =
      new AuthCredentialToUpdateDTO(FIRSTNAME, LASTNAME, DATE_OF_BIRTH, EMAIL, STRING_USERNAME_ROLE_USER);
}

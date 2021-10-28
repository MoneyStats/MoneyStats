package com.moneystats.authentication;

import com.moneystats.authentication.DTO.*;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/credential")
@OpenAPIDefinition(tags = {@Tag(name = "MoneyStats", description = "")})
public class AuthCredentialController {

  @Autowired private AuthCredentialService service;

  @PostMapping("/login")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.POST_LOGIN_SUMMARY,
      description = SchemaDescription.POST_LOGIN_DESCRIPTION,
      tags = "AuthCredential")
  public TokenDTO loginUser(@RequestBody AuthCredentialInputDTO userCredential)
      throws AuthenticationException {
    return service.login(userCredential);
  }

  @PostMapping("/signup")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.POST_SIGN_UP_SUMMARY,
      description = SchemaDescription.POST_SIGN_UP_DESCRIPTION,
      tags = "AuthCredential")
  public AuthResponseDTO addUser(@RequestBody AuthCredentialDTO userCredential)
      throws AuthenticationException {
    return service.signUp(userCredential);
  }

  @GetMapping("/token")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_USER_WITH_TOKEN_SUMMARY,
      description = SchemaDescription.GET_USER_WITH_TOKEN_DESCRIPTION,
      tags = "AuthCredential")
  public AuthCredentialDTO tokenUser(@RequestHeader(value = "Authorization") String jwt)
      throws AuthenticationException {
    TokenDTO token = new TokenDTO(jwt);
    return service.getUser(token);
  }

  @GetMapping("/admin")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_ADMIN_USER_SUMMARY,
      description = SchemaDescription.GET_ADMIN_USER_DESCRIPTION,
      tags = "AuthCredential")
  public List<AuthCredentialDTO> adminListUsers(@RequestHeader(value = "Authorization") String jwt)
      throws AuthenticationException {
    TokenDTO token = new TokenDTO(jwt);
    return service.getUsers(token);
  }

  @PutMapping("/update")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.PUT_UPDATE_USER_SUMMARY,
      description = SchemaDescription.PUT_UPDATE_USER_DESCRIPTION,
      tags = "AuthCredential")
  public AuthResponseDTO updateUser(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody AuthCredentialToUpdateDTO authCredentialToUpdateDTO)
      throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return service.updateUser(authCredentialToUpdateDTO, tokenDTO);
  }

  @GetMapping("/getCurrentUser")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_CURRENT_USER_SUMMARY,
      description = SchemaDescription.GET_CURRENT_USER_DESCRIPTION,
      tags = "AuthCredential")
  public AuthCredentialDTO getCurrentUser(@RequestHeader(value = "Authorization") String jwt)
      throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return service.getUpdateUser(tokenDTO);
  }

  @PutMapping("/update/password")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.POST_UPDATE_PASSWORD_SUMMARY,
      description = SchemaDescription.POST_UPDATE_PASSWORD_DESCRIPTION,
      tags = "AuthCredential")
  public AuthResponseDTO updatePassword(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody AuthChangePasswordInputDTO authChangePasswordInputDTO)
      throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return service.updatePassword(authChangePasswordInputDTO, tokenDTO);
  }
}

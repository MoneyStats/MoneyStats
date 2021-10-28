package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.DTO.StatementInputDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementResponseDTO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/statement")
@OpenAPIDefinition(tags = {@Tag(name = "Statement", description = "")})
public class StatementController {

  @Autowired private StatementService statementService;

  /**
   * @param jwt token into header
   * @param statement to be added
   * @return response of status
   * @throws StatementException
   * @throws WalletException
   * @throws AuthenticationException
   */
  @PostMapping("/addStatement")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.POST_ADD_STATEMENT_SUMMARY,
      description = SchemaDescription.POST_ADD_STATEMENT_DESCRIPTION,
      tags = "Statement")
  public StatementResponseDTO addStatement(
      @RequestHeader(value = "Authorization") String jwt, @RequestBody StatementInputDTO statement)
      throws StatementException, WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.addStatement(tokenDTO, statement);
  }

  /**
   * @param jwt token for authentications
   * @return a list of date
   * @throws StatementException
   * @throws WalletException
   * @throws AuthenticationException
   */
  @GetMapping("/listOfDate")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_LIST_OF_DATE_SUMMARY,
      description = SchemaDescription.GET_LIST_OF_DATE_DESCRIPTION,
      tags = "Statement")
  public List<String> listOfDate(@RequestHeader(value = "Authorization") String jwt)
      throws StatementException, WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.listOfDate(tokenDTO);
  }

  /**
   * Used to return the statement by that day
   *
   * @param jwt token for authentication
   * @param date date user for serching
   * @return
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/listStatementDate/{date}")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_LIST_STATEMENT_BY_DATE_SUMMARY,
      description = SchemaDescription.GET_LIST_STATEMENT_BY_DATE_DESCRIPTION,
      tags = "Statement")
  public List<StatementEntity> listByDate(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.listStatementByDate(tokenDTO, date);
  }
}

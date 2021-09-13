package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementInputDTO;
import com.moneystats.MoneyStats.commStats.statement.DTO.StatementResponseDTO;
import com.moneystats.MoneyStats.commStats.statement.entity.StatementEntity;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statement")
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
  public List<StatementEntity> listByDate(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.listStatementByDate(tokenDTO, date);
  }
}

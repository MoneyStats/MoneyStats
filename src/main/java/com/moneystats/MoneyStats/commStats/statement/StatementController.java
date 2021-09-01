package com.moneystats.MoneyStats.commStats.statement;

import com.moneystats.MoneyStats.commStats.statement.DTO.StatementDTO;
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

  @PostMapping("/addStatement")
  public StatementResponseDTO addStatement(
      @RequestHeader(value = "Authorization") String jwt, @RequestBody StatementDTO statement)
      throws StatementException, WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.addStatement(tokenDTO, statement);
  }

  @GetMapping("/listOfDate")
  public List<String> listOfDate(@RequestHeader(value = "Authorization") String jwt)
      throws StatementException, WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.listOfDate(tokenDTO);
  }

  @GetMapping("/listStatementDate/{date}")
  public List<StatementEntity> listByDate(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws StatementException, WalletException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return statementService.listStatementByDate(tokenDTO, date);
  }

  //@GetMapping("/listStatement")
  //public List<String> listByWalletAndValue(@RequestHeader(value = "Authorization") String jwt)
  //    throws StatementException, WalletException, AuthenticationException {
  //  TokenDTO tokenDTO = new TokenDTO(jwt);
  //  return statementService.listByWalletAndValue(tokenDTO);
  //}
}

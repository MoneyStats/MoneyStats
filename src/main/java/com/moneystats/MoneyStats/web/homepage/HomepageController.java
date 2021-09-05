package com.moneystats.MoneyStats.web.homepage;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homepage")
public class HomepageController {

  @Autowired private HomepageService homepageService;

  @GetMapping("/reportHomepage")
  public HomepageReportDTO statementReportHomepage(
      @RequestHeader(value = "Authorization") String jwt)
      throws StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.reportHomepage(tokenDTO);
  }

  @GetMapping("/getPieGraph/{date}")
  public HomepagePieChartDTO homepageGraph(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws WalletException, StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.getGraph(date, tokenDTO);
  }
}

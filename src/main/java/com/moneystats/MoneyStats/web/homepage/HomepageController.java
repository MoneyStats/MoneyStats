package com.moneystats.MoneyStats.web.homepage;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/homepage")
public class HomepageController {

  @Autowired private HomepageService homepageService;

  /**
   * Get report of the Homepage at the start of web app
   * @param jwt
   * @return
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/reportHomepage")
  public HomepageReportDTO statementReportHomepage(
      @RequestHeader(value = "Authorization") String jwt)
          throws StatementException, AuthenticationException, ParseException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.reportHomepage(tokenDTO);
  }

  /**
   * Get Pie Graph on Homepage
   * @param jwt for auth
   * @param date to be searched
   * @return Data for let me get the graph
   * @throws WalletException
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/getPieGraph/{date}")
  public HomepagePieChartDTO homepageGraph(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws WalletException, StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.getGraph(date, tokenDTO);
  }
}

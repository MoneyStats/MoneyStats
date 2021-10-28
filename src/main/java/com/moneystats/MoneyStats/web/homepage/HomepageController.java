package com.moneystats.MoneyStats.web.homepage;

import com.moneystats.MoneyStats.commStats.statement.StatementException;
import com.moneystats.MoneyStats.commStats.wallet.WalletException;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepagePieChartDTO;
import com.moneystats.MoneyStats.web.homepage.DTO.HomepageReportDTO;
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
import java.text.ParseException;

@RestController
@RequestMapping("/homepage")
@OpenAPIDefinition(tags = {@Tag(name = "Homepage", description = "")})
public class HomepageController {

  @Autowired private HomepageService homepageService;

  /**
   * Get report of the Homepage at the start of web app
   *
   * @param jwt
   * @return
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/reportHomepage")
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_REPORT_HOMEPAGE_SUMMARY,
      description = SchemaDescription.GET_REPORT_HOMEPAGE_DESCRIPTION,
      tags = "Homepage")
  public HomepageReportDTO statementReportHomepage(
      @RequestHeader(value = "Authorization") String jwt)
      throws StatementException, AuthenticationException, ParseException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.reportHomepage(tokenDTO);
  }

  /**
   * Get Pie Graph on Homepage
   *
   * @param jwt for auth
   * @param date to be searched
   * @return Data for let me get the graph
   * @throws WalletException
   * @throws StatementException
   * @throws AuthenticationException
   */
  @GetMapping("/getPieGraph/{date}")
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_PIE_GRAPH_SUMMARY,
      description = SchemaDescription.GET_PIE_GRAPH_DESCRIPTION,
      tags = "Homepage")
  public HomepagePieChartDTO homepageGraph(
      @RequestHeader(value = "Authorization") String jwt, @PathVariable String date)
      throws WalletException, StatementException, AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return homepageService.getGraph(date, tokenDTO);
  }
}

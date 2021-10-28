package com.moneystats.MoneyStats.web;

import com.moneystats.authentication.SecurityRoles;
import com.moneystats.generic.SchemaDescription;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.security.RolesAllowed;

@Controller
@OpenAPIDefinition(tags = {@Tag(name = "WebController", description = "")})
public class WebController {

  /**
   * If tipe localhost:8080/ it returns the login page
   *
   * @return
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  @RolesAllowed({SecurityRoles.MONEYSTATS_USER_ROLE, SecurityRoles.MONEYSTATS_ADMIN_ROLE})
  @Operation(
      summary = SchemaDescription.GET_LOGINPAGE_SUMMARY,
      description = SchemaDescription.GET_LOGINPAGE_DESCRIPTION,
      tags = "WebController")
  public String index() {
    return "loginpage.html";
  }

  /**
   * On press of logout on the html
   *
   * @return loginPage
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  @RolesAllowed({SecurityRoles.MONEYSTATS_ADMIN_ROLE, SecurityRoles.MONEYSTATS_USER_ROLE})
  @Operation(
      summary = SchemaDescription.GET_LOGOUT_SUMMARY,
      description = SchemaDescription.GET_LOGOUT_DESCRIPTION,
      tags = "WebController")
  public String logout() {
    return "loginpage.html";
  }
}

package com.moneystats.MoneyStats.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {

  /**
   * If tipe localhost:8080/ it returns the login page
   *
   * @return
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index() {
    return "loginpage.html";
  }

  /**
   * On press of logout on the html
   *
   * @return loginPage
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout() {
    return "loginpage.html";
  }
}

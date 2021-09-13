package com.moneystats.MoneyStats.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

  /**
   * If tipe localhost:8080/ it returns the login page
   * @return
   */
  @RequestMapping("/")
  public String index() {
    return "loginPage.html";
  }

  /**
   * On press of logout on the html
   * @return loginPage
   */
  @RequestMapping("/logout")
  public String logout() {
    return "loginPage.html";
  }
}

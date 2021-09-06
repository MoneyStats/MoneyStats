package com.moneystats.MoneyStats.web;

import com.moneystats.authentication.DTO.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class WebControllerLogin {

  @Autowired private WebService webService;

  @GetMapping("/check_login")
  public void checkLogin(@RequestHeader(value = "Authorization") String jwt) throws WebException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    webService.parseToken(tokenDTO);
  }
}

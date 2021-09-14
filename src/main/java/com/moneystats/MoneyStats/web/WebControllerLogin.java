package com.moneystats.MoneyStats.web;

import com.moneystats.MoneyStats.web.DTO.WebResponseDTO;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import jdk.jfr.StackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class WebControllerLogin {

  @Autowired private WebService webService;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  /**
   * Methods with no return, it need to check if the token it is still valid
   * @param jwt
   * @return User
   * @throws WebException
   */
  @GetMapping("/check_login")
  public AuthCredentialDTO checkLogin(@RequestHeader(value = "Authorization") String jwt) throws WebException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    LOG.info("Login Validations Process {}", jwt);
    return webService.checkUser(tokenDTO);
  }
}

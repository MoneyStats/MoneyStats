package com.moneystats.MoneyStats.web;

import com.moneystats.authentication.AuthenticationException;
import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class WebService {

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private static final String FIRSTNAME = "firstName";
  private static final String LASTNAME = "lastName";
  private static final String DATEOFBIRTH = "dateOfBirth";
  private static final String EMAIL = "email";
  private static final String ROLE = "role";

  @Value(value = "${jwt.secret}")
  private String secret;

  @Value(value = "${jwt.time}")
  private String expirationTime;

  public void parseToken(TokenDTO token) throws WebException {
    try {
      Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token.getAccessToken()).getBody();
    } catch (JwtException e) {
      LOG.error("Not Authorized, Login required WebService:38");
      throw new WebException(WebException.Code.LOGIN_REQUIRED);
    }
  }
}

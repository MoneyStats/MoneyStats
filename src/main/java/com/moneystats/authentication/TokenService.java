package com.moneystats.authentication;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.TokenDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

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

  public TokenDTO generateToken(AuthCredentialDTO user) {
    Claims claims = Jwts.claims().setSubject(user.getUsername());
    claims.put(FIRSTNAME, user.getFirstName());
    claims.put(LASTNAME, user.getLastName());
    claims.put(DATEOFBIRTH, user.getDateOfBirth());
    claims.put(EMAIL, user.getEmail());
    claims.put(ROLE, user.getRole());
    long dateExp = Long.parseLong(expirationTime);
    Date exp = new Date(System.currentTimeMillis() + dateExp);
    String token =
        Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS512, secret)
            .setExpiration(exp)
            .compact();
    return new TokenDTO(token);
  }

  public AuthCredentialDTO parseToken(TokenDTO token) throws AuthenticationException {
    Claims body;
    try {
      body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token.getAccessToken()).getBody();
    } catch (JwtException e) {
      LOG.error("Not Authorized, parse Token");
      throw new AuthenticationException(AuthenticationException.Code.UNAUTHORIZED);
    }
    AuthCredentialDTO user =
        new AuthCredentialDTO(
            (@NotNull String) body.get(FIRSTNAME),
            (@NotNull String) body.get(LASTNAME),
            (@NotNull String) body.get(DATEOFBIRTH),
            (@NotNull String) body.get(EMAIL),
            body.getSubject(),
            (@NotNull String) body.get(ROLE));
    return user;
  }
}

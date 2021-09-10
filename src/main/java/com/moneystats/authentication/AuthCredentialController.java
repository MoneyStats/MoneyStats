package com.moneystats.authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;

@RestController
@RequestMapping("/credential")
public class AuthCredentialController {

  @Autowired private AuthCredentialService service;

  @PostMapping("/signup")
  public AuthResponseDTO addUser(@RequestBody AuthCredentialDTO userCredential)
      throws AuthenticationException {
    return service.signUp(userCredential);
  }

  @PostMapping("/login")
  public TokenDTO loginUser(@RequestBody AuthCredentialInputDTO userCredential)
      throws AuthenticationException {
    return service.login(userCredential);
  }

  @GetMapping("/token")
  public AuthCredentialDTO tokenUser(@RequestHeader(value = "Authorization") String jwt)
      throws AuthenticationException {
    TokenDTO token = new TokenDTO(jwt);
    return service.getUser(token);
  }

  @GetMapping("/admin")
  public List<AuthCredentialDTO> adminListUsers(@RequestHeader(value = "Authorization") String jwt)
      throws AuthenticationException {
    TokenDTO token = new TokenDTO(jwt);
    return service.getUsers(token);
  }
}

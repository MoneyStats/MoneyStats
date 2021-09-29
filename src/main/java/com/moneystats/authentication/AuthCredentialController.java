package com.moneystats.authentication;

import com.moneystats.authentication.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

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
      throws AuthenticationException, UnknownHostException {
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

  @PutMapping("/update")
  public AuthResponseDTO updateUser(
      @RequestHeader(value = "Authorization") String jwt,
      @RequestBody AuthCredentialToUpdateDTO authCredentialToUpdateDTO)
      throws AuthenticationException {
    TokenDTO tokenDTO = new TokenDTO(jwt);
    return service.updateUser(authCredentialToUpdateDTO, tokenDTO);
  }
}

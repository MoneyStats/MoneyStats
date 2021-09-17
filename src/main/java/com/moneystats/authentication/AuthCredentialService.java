package com.moneystats.authentication;

import com.moneystats.authentication.DTO.AuthCredentialDTO;
import com.moneystats.authentication.DTO.AuthCredentialInputDTO;
import com.moneystats.authentication.DTO.AuthResponseDTO;
import com.moneystats.authentication.DTO.TokenDTO;
import com.moneystats.authentication.entity.AuthCredentialEntity;
import com.moneystats.generic.SchemaDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthCredentialService {

  @Autowired AuthCredentialDAO authCredentialDAO;
  @Autowired TokenService tokenService;
  @Autowired HttpServletRequest httpServletRequest;

  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  private static final Logger LOG = LoggerFactory.getLogger(AuthCredentialService.class);

  /**
   * Process to signUp with a new user
   * @param userCredential to be stored
   * @return A response of success
   * @throws AuthenticationException on invalid input
   */
  public AuthResponseDTO signUp(AuthCredentialDTO userCredential) throws AuthenticationException {
    userCredential.setRole(SecurityRoles.MONEYSTATS_USER_ROLE);
    AuthenticationValidator.validateAuthCredentialDTO(userCredential);
    AuthCredentialInputDTO authCredentialInputDTO =
        new AuthCredentialInputDTO(userCredential.getUsername(), userCredential.getPassword());
    AuthCredentialEntity authCredentialEntity =
        authCredentialDAO.getCredential(authCredentialInputDTO);
    if (authCredentialEntity != null) {
      LOG.error("Username Present, needs different");
      throw new AuthenticationException(AuthenticationException.Code.USER_PRESENT);
    }
    userCredential.setPassword(bCryptPasswordEncoder.encode(userCredential.getPassword()));
    authCredentialDAO.insertUserCredential(userCredential);
    return new AuthResponseDTO(SchemaDescription.USER_ADDED_CORRECT);
  }

  /**
   * Process to login via input
   * @param userCredential input
   * @return TokenDTO
   * @throws AuthenticationException on Token
   */
  public TokenDTO login(AuthCredentialInputDTO userCredential) throws AuthenticationException {
    AuthenticationValidator.validateAuthCredentialInputDTO(userCredential);
    AuthCredentialEntity userEntity = authCredentialDAO.getCredential(userCredential);
    if (userEntity == null) {
      LOG.error("User Not Found, username");
      throw new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
    }
    boolean matches =
        bCryptPasswordEncoder.matches(userCredential.getPassword(), userEntity.getPassword());
    if (!matches) {
      LOG.error("User Not Found, password");
      throw new AuthenticationException(AuthenticationException.Code.WRONG_CREDENTIAL);
    }

    String remoteAddress = httpServletRequest.getRemoteAddr();
    String userAgent = httpServletRequest.getHeader("Host") + " - " + httpServletRequest.getHeader("User-Agent");

    LOG.warn("IP Address Connected: {} and Hostname: {} and Remote Address {}", httpServletRequest.getRemoteHost(), httpServletRequest.getServerName(), remoteAddress);
    LOG.warn("IP Address Connected to User Agent: {}", userAgent);
    AuthCredentialDTO userDto =
        new AuthCredentialDTO(
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getDateOfBirth(),
            userEntity.getEmail(),
            userEntity.getUsername(),
            userEntity.getRole());
    return tokenService.generateToken(userDto);
  }

  /**
   * Process to return the user logged
   * @param token to be check
   * @return An user
   * @throws AuthenticationException for token
   */
  public AuthCredentialDTO getUser(TokenDTO token) throws AuthenticationException {
    TokenValidation.validateTokenDTO(token);
    return tokenService.parseToken(token);
  }

  /**
   * Methods to get all the user
   * @param token ADMIN param
   * @return list of user into db
   * @throws AuthenticationException parsing Token
   */
  public List<AuthCredentialDTO> getUsers(TokenDTO token) throws AuthenticationException {
    TokenValidation.validateTokenDTO(token);
    AuthCredentialDTO user = tokenService.parseToken(token);

    if (!user.getRole().equalsIgnoreCase(SecurityRoles.MONEYSTATS_ADMIN_ROLE)) {
      LOG.error("Not Allowed, SecurityRoles");
      throw new AuthenticationException(AuthenticationException.Code.NOT_ALLOWED);
    }
    List<AuthCredentialDTO> listUsers = new ArrayList<>();
    List<AuthCredentialEntity> list = authCredentialDAO.getUsers();
    for (AuthCredentialEntity authCredentialEntity : list) {
      listUsers.add(
          new AuthCredentialDTO(
              authCredentialEntity.getFirstName(),
              authCredentialEntity.getLastName(),
              authCredentialEntity.getDateOfBirth(),
              authCredentialEntity.getEmail(),
              authCredentialEntity.getUsername(),
              authCredentialEntity.getRole()));
    }
    return listUsers;
  }
}

package com.sam.userservice.filter;

import antlr.Token;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.userservice.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sam.userservice.utils.StringUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String userName = request.getParameter("username");
    String password = request.getParameter("password");
    log.info("Username is: {}", userName);
    log.info("Password is: {}", password);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userName, password);
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException, ServletException {
    User user = (User) authentication.getPrincipal();
    Algorithm algorithm =
        Algorithm.HMAC256("secret".getBytes()); // TODO change "secret" to a secure string
    //    String accessToken = getAccessToken(user, request, algorithm);
    String accessToken = getAccessToken(user, request, algorithm);
    String refreshToken = getRefreshToken(user, request, algorithm);
    //    response.setHeader("access_token", access_token);
    //    response.setHeader("refresh_token", refresh_token);
    response.setContentType(APPLICATION_JSON_VALUE);
    new ObjectMapper()
        .writeValue(response.getOutputStream(), TokenUtils.getTokenMap(accessToken, refreshToken));
  }

  public static String getAccessToken(User user, HttpServletRequest request, Algorithm algorithm) {
    return TokenUtils.prepareAccessToken(user.getUsername(), request, 10 * 60 * 1000)
        .withClaim(
            ROLES,
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
        .sign(algorithm);
  }

  public static String getRefreshToken(User user, HttpServletRequest request, Algorithm algorithm) {
    return JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
        .withIssuer(request.getRequestURI().toString())
        .sign(algorithm);
  }
}

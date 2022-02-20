package com.sam.userservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sam.userservice.utils.StringUtils.ACCESS_TOKEN;
import static com.sam.userservice.utils.StringUtils.REFRESH_TOKEN;

public class TokenUtils {

  public static Map<String, String> getTokenMap(String accessToken, String refreshToken) {
    Map<String, String> tokens = new HashMap<>();
    tokens.put(ACCESS_TOKEN, accessToken);
    tokens.put(REFRESH_TOKEN, refreshToken);
    return tokens;
  }

  public static JWTCreator.Builder prepareAccessToken(String userName, HttpServletRequest request, int expirationTime){
    return JWT.create()
            .withSubject(userName)
            .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
            .withIssuer(request.getRequestURI().toString());
  }

}

package com.sam.userservice.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.userservice.domain.Role;
import com.sam.userservice.domain.User;
import com.sam.userservice.service.UserService;
import com.sam.userservice.utils.TokenUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sam.userservice.filter.CustomAuthorizationFilter.BEARER;
import static com.sam.userservice.utils.StringUtils.ROLES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserResource {
  private final UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok().body(userService.getUsers());
  }

  @PostMapping("/user/save")
  public ResponseEntity<User> saveUser(@RequestBody User user) {
    URI uri =
        URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/user/save")
                .toUriString());
    return ResponseEntity.created(uri).body(userService.saveUser(user));
  }

  @PostMapping("/role/save")
  public ResponseEntity<Role> saveRole(@RequestBody Role role) {
    URI uri =
        URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/role/save")
                .toUriString());
    return ResponseEntity.created(uri).body(userService.saveRole(role));
  }

  @PostMapping("/role/addtouser")
  public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
    userService.addRoleToUser(form.getUserName(), form.getRoleName());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
      try {
        String refreshToken = authorizationHeader.substring(BEARER.length());
        Algorithm algorithm =
            Algorithm.HMAC256("secret".getBytes()); // TODO change "secret" to a secure string
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String userName = decodedJWT.getSubject();
        User user = userService.getUser(userName);
        String accessToken = getAccessToken(user, request, algorithm);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
            .writeValue(
                response.getOutputStream(), TokenUtils.getTokenMap(accessToken, refreshToken));
      } catch (Exception e) {
        log.error("Error logging in: {}", e.getMessage());
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
      }
    } else {
      throw new RuntimeException("Refresh token is missing.");
    }
  }

  public static String getAccessToken(User user, HttpServletRequest request, Algorithm algorithm) {
    return TokenUtils.prepareAccessToken(user.getUserName(), request, 10 * 60 * 1000)
        .withClaim(ROLES, user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
        .sign(algorithm);
  }

  @Data
  class RoleToUserForm {
    private String userName;
    private String roleName;
  }
}

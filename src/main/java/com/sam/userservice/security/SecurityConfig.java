package com.sam.userservice.security;

import com.sam.userservice.filter.CustomAuthenticationFilter;
import com.sam.userservice.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.sam.userservice.utils.APIUtils.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CustomAuthenticationFilter customAuthenticationFilter =
        new CustomAuthenticationFilter(authenticationManagerBean());
    customAuthenticationFilter.setFilterProcessesUrl(API_LOGIN);
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(STATELESS);
    http.authorizeRequests()
        .antMatchers(API_LOGIN + ALLOW_ALL_PATH, API_TOKEN_REFRESH + ALLOW_ALL_PATH)
        .permitAll();
    http.authorizeRequests().antMatchers(GET, API_USER_ALL).hasAnyAuthority("ROLE_USER");
    http.authorizeRequests().antMatchers(POST, API_USER_SAVE_ALL).hasAnyAuthority("ROLE_ADMIN");
    http.authorizeRequests().anyRequest().authenticated();
    http.addFilter(customAuthenticationFilter); // Authentication
    // intercept any request before any other filters
    http.addFilterBefore( // Authorization
        new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}

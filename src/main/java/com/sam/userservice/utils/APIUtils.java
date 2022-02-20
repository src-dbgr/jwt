package com.sam.userservice.utils;

public class APIUtils {
  public static final String ALLOW_ALL_PATH = "/**";
  public static final String API = "/api";
  public static final String LOGIN = "/login";
  public static final String USER = "/user";
  public static final String TOKEN = "/token";
  public static final String SAVE = "/save";
  public static final String REFRESH = "/refresh";
  public static final String API_LOGIN = API + LOGIN;
  public static final String API_LOGIN_ALL = API + LOGIN + ALLOW_ALL_PATH;
  public static final String API_TOKEN_REFRESH = API + TOKEN + REFRESH;
  public static final String API_TOKEN_REFRESH_ALL = API + TOKEN + REFRESH + ALLOW_ALL_PATH;
  public static final String API_USER = API + USER;
  public static final String API_USER_ALL = API + USER + ALLOW_ALL_PATH;
  public static final String API_USER_SAVE = API + USER + SAVE;
  public static final String API_USER_SAVE_ALL = API + USER + SAVE + ALLOW_ALL_PATH;
}

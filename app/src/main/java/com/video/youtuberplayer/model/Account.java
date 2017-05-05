package com.video.youtuberplayer.model;

import java.io.Serializable;

/**
 * Created by hoanghiep on 5/5/17.
 */

public class Account implements Serializable {
  private String name;
  private String token;
  private String scope;

  public Account(String name, String token, String scope) {
    this.name = name;
    this.token = token;
    this.scope = scope;
  }

  public String getName() {
    return name;
  }

  public String getToken() {
    return token;
  }

  public String getScope() {
    return scope;
  }
}

package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UsernameAlreadyInUseException extends HandledException {

  private final String username;

  public UsernameAlreadyInUseException(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUsernameAlreadyInUse(ctx, this);
  }
}

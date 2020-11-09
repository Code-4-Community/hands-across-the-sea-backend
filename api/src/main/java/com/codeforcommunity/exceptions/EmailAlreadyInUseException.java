package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class EmailAlreadyInUseException extends HandledException {
  private final String email;

  public EmailAlreadyInUseException(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleEmailAlreadyInUse(ctx, this);
  }
}

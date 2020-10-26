package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class AuthException extends HandledException {

  public AuthException(String message) {
    super(message);
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleAuth(ctx);
  }
}

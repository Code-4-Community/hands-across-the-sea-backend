package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class InvalidPasswordException extends HandledException {

  public InvalidPasswordException() {
    super();
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleInvalidPassword(ctx);
  }
}

package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class DisableOwnAccountException extends HandledException {

  public DisableOwnAccountException() {}

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleDisableOwnAccount(ctx);
  }
}

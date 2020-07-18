package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MissingBodyException extends HandledException {
  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleMissingBody(ctx);
  }
}

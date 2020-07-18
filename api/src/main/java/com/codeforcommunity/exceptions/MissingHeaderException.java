package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MissingHeaderException extends HandledException {

  private final String missingHeaderName;

  public MissingHeaderException(String missingHeaderName) {
    this.missingHeaderName = missingHeaderName;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleMissingHeader(ctx, this);
  }

  public String getMissingHeaderName() {
    return missingHeaderName;
  }
}

package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class MissingParameterException extends HandledException {

  private final String missingParameterName;

  public MissingParameterException(String missingParameterName) {
    this.missingParameterName = missingParameterName;
  }

  public String getMissingParameterName() {
    return this.missingParameterName;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleMissingParameter(ctx, this);
  }
}

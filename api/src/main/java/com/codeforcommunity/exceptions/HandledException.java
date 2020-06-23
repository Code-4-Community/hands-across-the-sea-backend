package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public abstract class HandledException extends RuntimeException {
  public HandledException() {
    super();
  }

  public HandledException(String message) {
    super(message);
  }

  public HandledException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public HandledException(Throwable throwable) {
    super(throwable);
  }

  public abstract void callHandler(FailureHandler handler, RoutingContext ctx);
}

package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UserDoesNotExistException extends HandledException {
  private int userId;

  public UserDoesNotExistException(int userId) {
    this.userId = userId;
  }

  public int getUserId() {
    return userId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUserDoesNotExist(ctx, this);
  }
}

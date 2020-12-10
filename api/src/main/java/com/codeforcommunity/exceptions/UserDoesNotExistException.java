package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UserDoesNotExistException extends HandledException {
  private String identifierMessage;

  public UserDoesNotExistException(long userId) {
    this.identifierMessage = "id = " + userId;
  }

  public UserDoesNotExistException(String email) {
    this.identifierMessage = "email = " + email;
  }

  public String getIdentifierMessage() {
    return this.identifierMessage;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUserDoesNotExist(ctx, this);
  }
}

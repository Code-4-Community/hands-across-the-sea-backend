package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolDoesNotExistException extends HandledException {


  public SchoolDoesNotExistException() {
    super();
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolDoesNotExist(ctx, this);
  }

}

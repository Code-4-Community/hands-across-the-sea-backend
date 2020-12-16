package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolDoesNotExistException extends HandledException {

  private String schoolName;

  public SchoolDoesNotExistException(String schoolName) {
    super();
    this.schoolName = schoolName;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolDoesNotExist(ctx, this);
  }

  public String getSchoolName() {
    return schoolName;
  }
}

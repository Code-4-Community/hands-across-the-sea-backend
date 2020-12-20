package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolDoesNotExistException extends HandledException {

  private int schoolId;

  public SchoolDoesNotExistException(int schoolId) {
    super();

    this.schoolId = schoolId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolDoesNotExist(ctx, this);
  }

  public int getSchoolId() {
    return schoolId;
  }
}

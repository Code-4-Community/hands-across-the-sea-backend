package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UnknownSchoolException extends HandledException {

  private int schoolId;

  public UnknownSchoolException(int schoolId) {
    super();

    this.schoolId = schoolId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUnknownSchool(ctx, this);
  }

  public int getSchoolId() {
    return schoolId;
  }
}

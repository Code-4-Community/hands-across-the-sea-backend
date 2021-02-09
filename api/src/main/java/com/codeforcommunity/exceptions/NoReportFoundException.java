package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class NoReportFoundException extends HandledException {

  private int schoolId;

  public NoReportFoundException(int schoolId) {
    super();

    this.schoolId = schoolId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleNoReportFound(ctx, this);
  }

  public int getSchoolId() {
    return schoolId;
  }

}

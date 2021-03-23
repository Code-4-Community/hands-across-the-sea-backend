package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class NoReportByIdFoundException extends HandledException {

  private int reportId;

  public NoReportByIdFoundException(int reportId) {
    super();

    this.reportId = reportId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleNoReportByIdFound(ctx, this);
  }

  public int getReportId() {
    return reportId;
  }
}

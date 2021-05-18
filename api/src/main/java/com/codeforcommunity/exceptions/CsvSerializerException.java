package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class CsvSerializerException extends HandledException {

  private int reportId;

  public CsvSerializerException(int reportId) {
    super();

    this.reportId = reportId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleCsvSerializer(ctx, this);
  }

  public int getReportId() {
    return reportId;
  }
}

package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class InvalidShipmentYearException extends HandledException {

  public Integer year;

  public InvalidShipmentYearException(Integer year) {
    super();
    this.year = year;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleInvalidShipmentYear(ctx, this);
  }

  public Integer getYear() {
    return this.year;
  }
}

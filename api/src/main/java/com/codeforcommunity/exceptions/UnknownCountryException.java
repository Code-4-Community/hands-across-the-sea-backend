package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UnknownCountryException extends HandledException {

  private String unknownCountry;

  public UnknownCountryException(String unknownCountry) {
    super();

    this.unknownCountry = unknownCountry;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUnknownCountry(ctx, this);
  }

  public String getUnknownCountry() {
    return unknownCountry;
  }
}

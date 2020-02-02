package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.exceptions.HandledException;

import io.vertx.ext.web.RoutingContext;

public class FailureHandler { //todo where should this file live?

  public void handleFailure(RoutingContext ctx) {
    Throwable throwable = ctx.failure();

    if(throwable instanceof HandledException) {
      ((HandledException) throwable).callHandler(this, ctx);
    } else {
      this.handleUncaughtError(ctx);
    }
  }

  public void handleAuth(RoutingContext ctx) {
    //todo implement
  }

  public void handleMissingParameter(RoutingContext ctx) {
    //todo implement
  }

  public void handleMissingHeader(RoutingContext ctx) {
    //todo implement
  }

  public void handleRequestBodyMapping(RoutingContext ctx) {
    //todo implement
  }

  private void handleUncaughtError(RoutingContext ctx){
    //todo implement 500 error handling
  }

  public void handleMissingBody(RoutingContext ctx) {
    //todo implement
  }

}

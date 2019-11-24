package com.codeforcommunity.rest;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public interface IRouter {

  Router initializeRouter(Vertx vertx);
}

package com.codeforcommunity.validation;

import com.codeforcommunity.auth.AuthProcessor;
import com.codeforcommunity.auth.AuthProcessorImpl;

import java.nio.file.Watchable;

import io.vertx.core.http.HttpServerRequest;

public class RequestValidatorImpl implements RequestValidator {

  AuthProcessor jwtValidation;

  @Override
  public boolean validateRequest(HttpServerRequest req) {
    try {
      this.jwtValidation = new AuthProcessorImpl();
      return jwtValidation.validate(req.headers().get("X-Access-Token"));
    } catch (Exception e) {
      return false;
    }
  }
}

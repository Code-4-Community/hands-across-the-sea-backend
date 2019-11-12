package com.codeforcommunity.validation;

import com.codeforcommunity.api.IAuthProcessor;

import io.vertx.core.http.HttpServerRequest;

public class RequestValidatorImpl implements RequestValidator {

  IAuthProcessor jwtValidation;

  /**
   * TODO: This implementation probably has to be rethought so that we don't have
   *  to give this class anything more than it needs.
   */
  public RequestValidatorImpl(IAuthProcessor jwtValidation) {
    this.jwtValidation = jwtValidation;
  }

  @Override
  public boolean validateRequest(HttpServerRequest req) {
    try {
      return jwtValidation.validate(req.headers().get("X-Access-Token"));
    } catch (Exception e) {
      return false;
    }
  }
}

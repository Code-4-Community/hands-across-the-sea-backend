package com.codeforcommunity.validation;

import io.vertx.core.http.HttpServerRequest;

public interface RequestValidator {

  /**
   * Validates a the form of an http request made to our server.
   * @param req request object.
   * @return true iff valid request.
   */
  boolean validateRequest(HttpServerRequest req);

}

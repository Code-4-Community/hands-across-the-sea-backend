package com.codeforcommunity.rest;

import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.MissingHeaderException;
import com.codeforcommunity.exceptions.MissingParameterException;
import com.codeforcommunity.exceptions.RequestBodyMappingException;

import java.util.Optional;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public interface RestFunctions {

  /**
   * Gets the JSON body from the given routing context and parses it into the given class.
   * @throws RequestBodyMappingException if the given request cannot be successfully mapped
   *      into the given class.
   * @throws RequestBodyMappingException if the given request does not have a body that can be
   *      parsed.
   */
  static <T> T getJsonBodyAsClass(RoutingContext ctx, Class<T> clazz) {
    Optional<JsonObject> body = Optional.ofNullable(ctx.getBodyAsJson());
    if (body.isPresent()) {
      try {
        return body.get().mapTo(clazz);
      } catch (IllegalArgumentException e) {
        throw new RequestBodyMappingException();
      }
    } else {
      throw new RequestBodyMappingException();
    }
  }

  static String getRequestHeader(HttpServerRequest req, String name) {
    String headerValue = req.getHeader(name);
    if (headerValue != null) {
      return headerValue;
    }
    throw new MissingHeaderException(name);
  }

  static int getRequestParameterAsInt(HttpServerRequest req, String name) {
    String paramValue = getRequestParameterAsString(req, name);
    try {
      return Integer.valueOf(paramValue);
    } catch (NumberFormatException ex) {
      throw new MalformedParameterException(name);
    }
  }

  static String getRequestParameterAsString(HttpServerRequest req, String name) {
    String paramValue = req.getParam(name);
    if (paramValue != null) {
      return paramValue;
    }
    throw new MissingParameterException(name);
  }

}

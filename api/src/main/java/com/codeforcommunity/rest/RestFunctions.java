package com.codeforcommunity.rest;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.MissingHeaderException;
import com.codeforcommunity.exceptions.MissingParameterException;
import com.codeforcommunity.exceptions.RequestBodyMappingException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;

public class RestFunctions {

  /**
   * Gets the JSON body from the given routing context, validates it, and parses it into the given
   * class.
   *
   * @throws RequestBodyMappingException if the given request cannot be successfully mapped into the
   *     given class.
   * @throws RequestBodyMappingException if the given request does not have a body that can be
   *     parsed.
   */
  public static <T extends ApiDto> T getJsonBodyAsClass(RoutingContext ctx, Class<T> clazz) {
    try {
      Optional<JsonObject> body = Optional.ofNullable(ctx.getBodyAsJson());
      T value = body.orElseThrow(RequestBodyMappingException::new).mapTo(clazz);
      value.validate();
      return value;
    } catch (IllegalArgumentException | DecodeException e) {
      e.printStackTrace();
      throw new RequestBodyMappingException();
    }
  }

  public static String getRequestHeader(HttpServerRequest req, String name) {
    String headerValue = req.getHeader(name);
    if (headerValue != null && !headerValue.isEmpty()) {
      return headerValue;
    }
    throw new MissingHeaderException(name);
  }

  public static int getRequestParameterAsInt(HttpServerRequest req, String name) {
    String paramValue = getRequestParameterAsString(req, name);
    try {
      return Integer.parseInt(paramValue);
    } catch (NumberFormatException ex) {
      throw new MalformedParameterException(name);
    }
  }

  public static String getRequestParameterAsString(HttpServerRequest req, String name) {
    String paramValue = req.getParam(name);
    if (paramValue != null && !paramValue.isEmpty()) {
      return paramValue;
    }
    throw new MissingParameterException(name);
  }

  public static boolean getRequestParameterAsBoolean(HttpServerRequest req, String name) {
    String paramValue = req.getParam(name);
    return Boolean.parseBoolean(paramValue);
  }

  public static int getPathParamAsInt(RoutingContext ctx, String paramName) {
    try {
      String paramValue = ctx.pathParam(paramName);
      return Integer.parseInt(paramValue);
    } catch (NumberFormatException ex) {
      throw new MalformedParameterException(paramName);
    }
  }
}

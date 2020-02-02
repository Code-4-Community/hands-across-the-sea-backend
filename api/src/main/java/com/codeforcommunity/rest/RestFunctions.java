package com.codeforcommunity.rest;

import com.codeforcommunity.exceptions.RequestBodyMappingException;

import java.util.Optional;

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

  static String getNullableString(String s, RuntimeException exception) {
    if(s != null) {
      return s;
    }
    throw exception;
  }

  static int getNullableStringAsInt(String s, RuntimeException exception) {
    try {
      return Integer.valueOf(s);
    } catch (NullPointerException | NumberFormatException ex) {
      throw exception;
    }
  }

}

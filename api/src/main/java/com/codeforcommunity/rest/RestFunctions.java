package com.codeforcommunity.rest;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.MissingHeaderException;
import com.codeforcommunity.exceptions.MissingParameterException;
import com.codeforcommunity.exceptions.RequestBodyMappingException;
import com.codeforcommunity.exceptions.UnknownCountryException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

  public static Country getCountryFromString(String countryName) {
    try {
      return Country.from(countryName);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw new UnknownCountryException(countryName);
    }
  }

  /**
   * Get's a query parameter that may or may not be there as an optional of the desired type.
   * Attempts to map the query parameter from a string to an instance of the desired type.
   *
   * @param ctx routing context to retrieve query param from.
   * @param name of query param.
   * @param mapper a function that maps the query param from string to desired type.
   * @param <T> the desired type.
   * @return An optional object of the query param as it's desired type.
   */
  public static <T> Optional<T> getOptionalQueryParam(
      RoutingContext ctx, String name, Function<String, T> mapper) {
    List<String> params = ctx.queryParam(name);
    T returnValue;
    if (!params.isEmpty()) {
      try {
        returnValue = mapper.apply(params.get(0));
      } catch (Throwable t) {
        throw new MalformedParameterException(name);
      }
    } else {
      returnValue = null;
    }
    return Optional.ofNullable(returnValue);
  }

  public static String getUpperSnakeFromCamel(String camel) {
    String regex = "([a-z])([A-Z])";
    String replacement = "$1_$2";
    return camel.replaceAll(regex, replacement).toUpperCase();
  }
}

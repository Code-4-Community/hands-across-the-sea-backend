package com.codeforcommunity.exceptions;

import com.codeforcommunity.enums.Country;
import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolAlreadyExistsException extends HandledException {

  private String schoolName;
  private Country schoolCountry;

  public SchoolAlreadyExistsException(String schoolName, Country schoolCountry) {
    super();

    this.schoolName = schoolName;
    this.schoolCountry = schoolCountry;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolAlreadyExists(ctx, this);
  }

  public String getSchoolName() {
    return schoolName;
  }

  public Country getSchoolCountry() {
    return schoolCountry;
  }
}

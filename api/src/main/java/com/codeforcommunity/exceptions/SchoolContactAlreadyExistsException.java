package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolContactAlreadyExistsException extends HandledException {

  private String schoolName;
  private String contactFirstName;
  private String contactLastName;

  public SchoolContactAlreadyExistsException(
      String schoolName, String contactFirstName, String contactLastName) {
    super();

    this.schoolName = schoolName;
    this.contactFirstName = contactFirstName;
    this.contactLastName = contactLastName;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolContactAlreadyExists(ctx, this);
  }

  public String getSchoolName() {
    return schoolName;
  }

  public String getContactFirstName() {
    return contactFirstName;
  }

  public String getContactLastName() {
    return contactLastName;
  }
}

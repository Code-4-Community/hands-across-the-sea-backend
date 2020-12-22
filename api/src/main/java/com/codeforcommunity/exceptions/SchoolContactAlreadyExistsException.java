package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class SchoolContactAlreadyExistsException extends HandledException {

  private String schoolName;
  private String contactName;

  public SchoolContactAlreadyExistsException(String schoolName, String contactName) {
    super();

    this.schoolName = schoolName;
    this.contactName = contactName;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleSchoolContactAlreadyExists(ctx, this);
  }

  public String getSchoolName() {
    return schoolName;
  }

  public String getContactName() {
    return contactName;
  }
}

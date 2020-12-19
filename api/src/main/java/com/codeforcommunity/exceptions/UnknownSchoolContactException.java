package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class UnknownSchoolContactException extends HandledException {

  private int schoolId;
  private int contactId;

  public UnknownSchoolContactException(int schoolId, int contactId) {
    super();

    this.schoolId = schoolId;
    this.contactId = contactId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleUnknownSchoolContact(ctx, this);
  }

  public int getSchoolId() {
    return schoolId;
  }

  public int getContactId() {
    return contactId;
  }
}

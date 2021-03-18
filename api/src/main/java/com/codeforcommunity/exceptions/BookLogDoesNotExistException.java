package com.codeforcommunity.exceptions;

import com.codeforcommunity.rest.FailureHandler;
import io.vertx.ext.web.RoutingContext;

public class BookLogDoesNotExistException extends HandledException {

  int bookId;

  public BookLogDoesNotExistException(int bookId) {
    this.bookId = bookId;
  }

  @Override
  public void callHandler(FailureHandler handler, RoutingContext ctx) {
    handler.handleBookLogDoesNotExist(ctx, this);
  }

  public int getBookId() {
    return this.bookId;
  }
}

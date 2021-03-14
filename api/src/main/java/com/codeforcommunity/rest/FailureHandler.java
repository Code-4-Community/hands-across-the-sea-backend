package com.codeforcommunity.rest;

import com.codeforcommunity.exceptions.CreateUserException;
import com.codeforcommunity.exceptions.EmailAlreadyInUseException;
import com.codeforcommunity.exceptions.ExpiredSecretKeyException;
import com.codeforcommunity.exceptions.HandledException;
import com.codeforcommunity.exceptions.InvalidSecretKeyException;
import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.MissingHeaderException;
import com.codeforcommunity.exceptions.MissingParameterException;
import com.codeforcommunity.exceptions.NoReportFoundException;
import com.codeforcommunity.exceptions.SchoolAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactDoesNotExistException;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.exceptions.TokenInvalidException;
import com.codeforcommunity.exceptions.UnknownCountryException;
import com.codeforcommunity.exceptions.UsedSecretKeyException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.exceptions.UsernameAlreadyInUseException;
import com.codeforcommunity.logger.SLogger;
import io.vertx.ext.web.RoutingContext;

public class FailureHandler {
  private final SLogger logger = new SLogger(FailureHandler.class);

  public void handleFailure(RoutingContext ctx) {
    Throwable throwable = ctx.failure();

    if (throwable instanceof HandledException) {
      ((HandledException) throwable).callHandler(this, ctx);
    } else {
      this.handleUncaughtError(ctx, throwable);
    }
  }

  public void handleAuth(RoutingContext ctx) {
    end(ctx, "Unauthorized user", 401);
  }

  public void handleMissingParameter(RoutingContext ctx, MissingParameterException e) {
    String message =
        String.format("Missing required path parameter: %s", e.getMissingParameterName());
    end(ctx, message, 400);
  }

  public void handleNoReportFound(RoutingContext ctx, NoReportFoundException e) {
    String message = String.format("Report not found for school with id %d", e.getSchoolId());
    end(ctx, message, 404);
  }

  public void handleAdminOnlyRoute(RoutingContext ctx) {
    String message = "This route is only available to admin users";
    end(ctx, message, 401);
  }

  public void handleEmailAlreadyInUse(RoutingContext ctx, EmailAlreadyInUseException exception) {
    String message =
        String.format("Error creating new user, given email %s already used", exception.getEmail());

    end(ctx, message, 409);
  }

  public void handleSchoolAlreadyExists(RoutingContext ctx, SchoolAlreadyExistsException e) {
    String message =
        String.format(
            "School '%s' already exists in '%s'", e.getSchoolName(), e.getSchoolCountry());
    end(ctx, message, 409);
  }

  public void handleSchoolContactAlreadyExists(
      RoutingContext ctx, SchoolContactAlreadyExistsException e) {
    String message =
        String.format(
            "Contact with name '%s %s' already exists for school '%s'",
            e.getContactFirstName(), e.getContactLastName(), e.getSchoolName());
    end(ctx, message, 409);
  }

  public void handleUsernameAlreadyInUse(
      RoutingContext ctx, UsernameAlreadyInUseException exception) {
    String message =
        String.format(
            "Error creating new user, given username %s already used", exception.getUsername());

    end(ctx, message, 409);
  }

  public void handleInvalidSecretKey(RoutingContext ctx, InvalidSecretKeyException exception) {
    String message = String.format("Given %s token is invalid", exception.getType());
    end(ctx, message, 401);
  }

  public void handleUsedSecretKey(RoutingContext ctx, UsedSecretKeyException exception) {
    String message = String.format("Given %s token has already been used", exception.getType());
    end(ctx, message, 401);
  }

  public void handleExpiredSecretKey(RoutingContext ctx, ExpiredSecretKeyException exception) {
    String message = String.format("Given %s token is expired", exception.getType());
    end(ctx, message, 401);
  }

  public void handleInvalidPassword(RoutingContext ctx) {
    String message = "Given password does not meet the security requirements";
    end(ctx, message, 400);
  }

  public void handleUserDoesNotExist(RoutingContext ctx, UserDoesNotExistException exception) {
    String message =
        String.format("No user with property <%s> exists", exception.getIdentifierMessage());
    end(ctx, message, 400);
  }

  public void handleMissingHeader(RoutingContext ctx, MissingHeaderException e) {
    String message = String.format("Missing required request header: %s", e.getMissingHeaderName());
    end(ctx, message, 400);
  }

  public void handleRequestBodyMapping(RoutingContext ctx) {
    String message = "Malformed JSON request body";
    end(ctx, message, 400);
  }

  public void handleMissingBody(RoutingContext ctx) {
    String message = "Missing required request body";
    end(ctx, message, 400);
  }

  public void handleCreateUser(RoutingContext ctx, CreateUserException exception) {
    CreateUserException.UsedField reason = exception.getUsedField();

    String reasonMessage =
        reason.equals(CreateUserException.UsedField.BOTH)
            ? "email and user name"
            : reason.toString();

    String message = String.format("Error creating new user, given %s already used", reasonMessage);

    end(ctx, message, 409);
  }

  public void handleTokenInvalid(RoutingContext ctx, TokenInvalidException e) {
    String message = String.format("Given %s token is expired or invalid", e.getTokenType());
    end(ctx, message, 401);
  }

  public void handleWrongPassword(RoutingContext ctx) {
    String message = "Given password is not correct";
    end(ctx, message, 401);
  }

  public void handleInvalidToken(RoutingContext ctx) {
    String message = "Given token is invalid";
    end(ctx, message, 401);
  }

  public void handleExpiredToken(RoutingContext ctx) {
    String message = "Given token is expired";
    end(ctx, message, 401);
  }

  public void handleMalformedParameter(RoutingContext ctx, MalformedParameterException exception) {
    String message =
        String.format("Given malformed parameter(s): %s", exception.getParameterName());
    end(ctx, message, 400);
  }

  public void handleUnknownCountry(RoutingContext ctx, UnknownCountryException exception) {
    String message = String.format("Unknown country given: %s", exception.getUnknownCountry());
    end(ctx, message, 400);
  }

  public void handleUnknownSchoolContact(
      RoutingContext ctx, SchoolContactDoesNotExistException exception) {
    String message =
        String.format(
            "Unknown contact with id '%d' given for school with id '%d'",
            exception.getContactId(), exception.getSchoolId());
    end(ctx, message, 400);
  }

  public void handleBadImageRequest(RoutingContext ctx) {
    String message = "The uploaded file could not be processed as an image";
    end(ctx, message, 400);
  }

  public void handleS3FailedUpload(RoutingContext ctx, String exceptionMessage) {
    String message = "The given file could not be uploaded to AWS S3: " + exceptionMessage;
    end(ctx, message, 502);
  }

  public void handleSchoolDoesNotExist(RoutingContext ctx, SchoolDoesNotExistException e) {
    String message = String.format("No school found with given id: %d", e.getSchoolId());
    end(ctx, message, 400);
  }


  private void handleUncaughtError(RoutingContext ctx, Throwable throwable) {
    String message = String.format("Internal server error caused by: %s", throwable.getMessage());
    logger.error(message);
    throwable.printStackTrace();
    end(ctx, message, 500);
  }

  private void end(RoutingContext ctx, String message, int statusCode) {
    ctx.response().setStatusCode(statusCode).end(message);
  }
}

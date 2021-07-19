package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.USERS;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

import com.codeforcommunity.api.authenticated.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.dataaccess.AuthDatabaseOperations;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.UserDataRequest;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.dto.user.UserListResponse;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.EmailAlreadyInUseException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.exceptions.WrongPasswordException;
import com.codeforcommunity.requester.Emailer;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Users;
import org.jooq.generated.tables.records.UsersRecord;

public class ProtectedUserProcessorImpl implements IProtectedUserProcessor {

  private final DSLContext db;
  private final Emailer emailer;

  public ProtectedUserProcessorImpl(DSLContext db, Emailer emailer) {
    this.db = db;
    this.emailer = emailer;
  }

  @Override
  public void deleteUser(JWTData userData) {
    int userId = userData.getUserId();

    db.deleteFrom(VERIFICATION_KEYS).where(VERIFICATION_KEYS.USER_ID.eq(userId)).executeAsync();

    UsersRecord user =
        db.selectFrom(USERS).where(USERS.ID.eq(userId).and(USERS.DELETED_AT.isNull())).fetchOne();

    user.setDeletedAt(Timestamp.from(Instant.now()));
    user.store();

    emailer.sendAccountDeactivatedEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public void changePassword(JWTData userData, ChangePasswordRequest changePasswordRequest) {
    int userId = userData.getUserId();

    UsersRecord user =
        db.selectFrom(USERS).where(USERS.ID.eq(userId).and(USERS.DELETED_AT.isNull())).fetchOne();

    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    if (Passwords.isExpectedPassword(
        changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
      user.setPasswordHash(Passwords.createHash(changePasswordRequest.getNewPassword()));
      user.store();
    } else {
      throw new WrongPasswordException();
    }

    emailer.sendPasswordChangeConfirmationEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public UserDataResponse getUserData(JWTData userData) {
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();

    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    return new UserDataResponse(
        user.getFirstName(),
        user.getLastName(),
        user.getId(),
        user.getEmail(),
        user.getCountry(),
        user.getPrivilegeLevel(),
        user.getDisabled());
  }

  @Override
  public void changeEmail(JWTData userData, ChangeEmailRequest changeEmailRequest) {
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();
    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    String previousEmail = user.getEmail();
    if (Passwords.isExpectedPassword(changeEmailRequest.getPassword(), user.getPasswordHash())) {
      if (db.fetchExists(USERS, USERS.EMAIL.eq(changeEmailRequest.getNewEmail()))) {
        throw new EmailAlreadyInUseException(changeEmailRequest.getNewEmail());
      }
      user.setEmail(changeEmailRequest.getNewEmail());
      user.store();
    } else {
      throw new WrongPasswordException();
    }

    emailer.sendEmailChangeConfirmationEmail(
        previousEmail,
        AuthDatabaseOperations.getFullName(user.into(Users.class)),
        changeEmailRequest.getNewEmail());
  }

  @Override
  public void updateUserData(JWTData userData, int userId, UserDataRequest request) {

    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();
    if (user == null) {
      throw new UserDoesNotExistException(userId);
    }

    user.setCountry(request.getCountry());
    user.setPrivilegeLevel(request.getPrivilegeLevel());
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.store();
  }

  @Override
  public void disableUserAccount(JWTData userData, int userId) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();
    if (user == null) {
      throw new UserDoesNotExistException(userId);
    }
    user.setDisabled(true);
    user.store();
  }

  @Override
  public UserListResponse getAllUsers(JWTData userData, Country country) {

    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    List<UserDataResponse> response = new ArrayList<>();
    List<UsersRecord> users = new ArrayList<>();

    if (country == null) {
      users = db.selectFrom(USERS).where(USERS.DELETED_AT.isNull()).fetch();
    } else {
      users =
          db.selectFrom(USERS)
              .where(USERS.COUNTRY.eq(country))
              .and(USERS.DELETED_AT.isNull())
              .fetch();
    }

    for (UsersRecord user : users) {
      response.add(
          new UserDataResponse(
              user.getFirstName(),
              user.getLastName(),
              user.getId(),
              user.getEmail(),
              user.getCountry(),
              user.getPrivilegeLevel(),
              user.getDisabled()));
    }

    return new UserListResponse(response);
  }
}

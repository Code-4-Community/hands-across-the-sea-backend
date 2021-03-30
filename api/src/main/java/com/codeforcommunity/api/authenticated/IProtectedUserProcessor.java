package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.GetAllUsersFromCountryRequest;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.enums.Country;
import java.util.List;

public interface IProtectedUserProcessor {

  /** Deletes the given user from the database. Does NOT invalidate the user's JWTs */
  void deleteUser(JWTData userData);

  /**
   * If the given current password matches the user's current password, update the user's password
   * to the new password value.
   */
  void changePassword(JWTData userData, ChangePasswordRequest changePasswordRequest);

  /** Get the user's data for use in the site. */
  UserDataResponse getUserData(JWTData userData);

  /** Change the user's email to the provided one */
  void changeEmail(JWTData userData, ChangeEmailRequest changeEmailRequest);

  List<UserDataResponse> getAllUsersFromCountry(JWTData userData, GetAllUsersFromCountryRequest request);

  List<UserDataResponse> getAllUsers(JWTData userData);
}

package com.codeforcommunity.dto;

import com.codeforcommunity.exceptions.HandledException;
import com.codeforcommunity.exceptions.MalformedParameterException;
import java.util.List;

public abstract class ApiDto {
  /**
   * Verify if the extending DTO is a valid object.
   *
   * @return A list of strings containing the fields that are invalid. Return a non-null an empty
   *     list if all fields are valid.
   * @throws HandledException if an issue comes up with a field that would not otherwise fall under
   *     a {@link MalformedParameterException}.
   */
  private List<String> validateFields() throws HandledException {
    return validateFields("");
  }

  /**
   * Verify if the extending DTO is a valid object.
   *
   * @param fieldPrefix A string to prefix each field with (for use if this is a sub-field). Should
   *     be of the form "OBJECT.".
   * @return A list of strings containing the fields that are invalid. Return a non-null empty list
   *     if all fields are valid.
   * @throws HandledException if an issue comes up with a field that would not otherwise fall under
   *     a {@link MalformedParameterException}.
   */
  public abstract List<String> validateFields(String fieldPrefix) throws HandledException;

  /**
   * Verify if the extending DTO is a valid object. This version should be overridden if this object
   * has sometimes-optional fields. For an example, see {@code EventDetails} in Lucy's Love Bus.
   *
   * @param fieldPrefix A string to prefix each field with 9for use if this is a sub-field). Should
   *     be of the form "OBJECT.".
   * @param nullable a boolean representing whether this is the nullable version of an object with
   *     sometimes-optional fields.
   * @return A list of strings containing the fields that are invalid. Return a non-null empty list
   *     if all fields are valid.
   * @throws HandledException if an issue comes up with a field that would not otherwise fall under
   *     a {@link MalformedParameterException}.
   */
  public List<String> validateFields(String fieldPrefix, boolean nullable) throws HandledException {
    return validateFields(fieldPrefix);
  }

  /**
   * Validate the extending DTO. Calls validateFields, joins the list of fields, and throws a {@link
   * HandledException} containing the field(s) that caused the issue. Can be overridden if another
   * {@link HandledException} should be thrown.
   *
   * @throws HandledException Containing the error fields or corresponding to any other C4C defined
   *     and handled errors that may come up.
   */
  public void validate() throws HandledException {
    List<String> fields = this.validateFields();
    if (fields == null) {
      throw new IllegalStateException("Field validation cannot return null value.");
    }
    if (fields.size() == 0) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < fields.size(); i++) {
      builder.append(fields.get(i));
      if (i < fields.size() - 1) {
        builder.append(", ");
      }
    }
    throw new MalformedParameterException(builder.toString());
  }

  /**
   * Checks to see if email is valid.
   *
   * @param email the email to check.
   * @return a boolean representing whether this email is not valid.
   */
  protected boolean emailInvalid(String email) {
    return email == null || !email.matches("^\\S+@\\S+\\.\\S{2,}$");
  }

  /**
   * Checks to see if the given string is empty.
   *
   * @param str the string to check.
   * @return a boolean representing whether this string is empty.
   */
  protected boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Checks to see if a password isn't null and it's length with whitespace trimmed is at least 8.
   *
   * @param pass the password to check.
   * @return a boolean representing whether the given password is invalid or not.
   */
  protected boolean passwordInvalid(String pass) {
    return pass == null || pass.trim().length() < 8;
  }
}

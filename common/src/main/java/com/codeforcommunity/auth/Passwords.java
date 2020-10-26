package com.codeforcommunity.auth;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A utility class to hash passwords and check passwords vs hashed values. It uses a combination of
 * hashing and unique salt. The algorithm used is PBKDF2WithHmacSHA1 which, although not the best
 * for hashing password (vs. bcrypt) is still considered robust and <a
 * href="https://security.stackexchange.com/a/6415/12614">recommended by NIST </a>. The hashed value
 * has 256 bits.
 */
public final class Passwords {
  private static final Random RANDOM = new SecureRandom();
  /** Number of iterations to run for hashing the password. */
  private static final int ITERATIONS = 10000;
  /** Key length for the hash. */
  public static final int KEY_LENGTH = 256;
  /** Salt length for the hash. */
  public static final int SALT_LENGTH = 16;
  /** Secret key algorithm. */
  private static final String SECRET_KEY_DERIVATION = "PBKDF2WithHmacSHA1";

  /**
   * Returns a random salt to be used to hash a password.
   *
   * @param length length of salt to be returned.
   * @return a bytes random salt.
   */
  private static byte[] getNextSalt(int length) {
    byte[] salt = new byte[length];
    RANDOM.nextBytes(salt);
    return salt;
  }

  /**
   * Creates a hash for the given password.
   *
   * @param password the password to hash (and salt).
   * @return a byte[] of the salt and hash.
   */
  public static byte[] createHash(String password) {
    byte[] salt = getNextSalt(SALT_LENGTH);

    return hash(password, salt);
  }

  /**
   * Returns a salted and hashed password using the provided hash.
   *
   * @param password the password to be hashed.
   * @param salt a byte[] of the salt.
   * @return the hashed password with a pinch of salt.
   */
  private static byte[] hash(String password, byte[] salt) {
    byte[] hashByteArr;
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_DERIVATION);
      hashByteArr = skf.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
    } finally {
      spec.clearPassword();
    }

    byte[] finalHash = new byte[hashByteArr.length + salt.length];
    for (int i = 0; i < hashByteArr.length; i++) {
      finalHash[i] = hashByteArr[i];
    }
    for (int i = 0; i < salt.length; i++) {
      finalHash[i + hashByteArr.length] = salt[i];
    }

    return finalHash;
  }

  /**
   * Returns true if the given password and salt match the hashed value, false otherwise.
   *
   * @param password the password to check.
   * @param expectedHash the expected hashed value of the password.
   * @return true if the given password and salt match the hashed value, false otherwise.
   */
  public static boolean isExpectedPassword(String password, byte[] expectedHash) {
    byte[] salt = new byte[SALT_LENGTH];
    for (int i = 0; i < SALT_LENGTH; i++) {
      salt[i] = expectedHash[expectedHash.length - SALT_LENGTH + i];
    }
    byte[] pwdHash = hash(password, salt);
    if (pwdHash.length != expectedHash.length) {
      return false;
    }
    for (int i = 0; i < expectedHash.length; i++) {
      if (expectedHash[i] != pwdHash[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Generates a random password of a given length, using letters and digits.
   *
   * @param length the length of the password.
   * @return a random password.
   */
  public static String generateRandomToken(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int c = RANDOM.nextInt(62);
      if (c <= 9) {
        sb.append(String.valueOf(c));
      } else if (c < 36) {
        sb.append((char) ('a' + c - 10));
      } else {
        sb.append((char) ('A' + c - 36));
      }
    }
    return sb.toString();
  }
}

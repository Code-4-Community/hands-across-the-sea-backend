package com.codeforcommunity.logger;

import java.sql.Timestamp;
import java.time.Instant;

public interface  Logger {

  static void log(String message) {

    String timestamp = String.format("%s: ", Timestamp.from(Instant.now()).toString());

    System.out.println(timestamp + message);

  }
}

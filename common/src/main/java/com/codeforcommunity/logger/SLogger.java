package com.codeforcommunity.logger;

import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SLogger {
  // The log4j logger to log errors encountered within this class
  private static final Logger log4jLogger = LogManager.getLogger(SLogger.class);
  // The webhook URL to post Slack messages to
  private static final String webhookUrl;
  // Whether or not Slack logging is enabled
  private static final boolean slackEnabled;
  // The client used to make web requests to Slack
  private static WebClient webClient;
  // The name of the product; the prefix of every message (e.g. "LLB", "SFTT", "HATS")
  private static String productName;
  // The logger used to log errors of the class that uses SLogger
  private final Logger classLogger;

  static {
    slackEnabled = Boolean.parseBoolean(PropertiesLoader.loadProperty("slack_enabled"));
    webhookUrl = PropertiesLoader.loadProperty("slack_webhook_url");
    productName = "C4C Product";
  }

  /**
   * Construct a logger instance for the given class.
   *
   * @param clazz the class using this logger instance.
   */
  public SLogger(Class<?> clazz) {
    this.classLogger = LogManager.getLogger(clazz);
  }

  /**
   * Log the given info-level message to the standard logger. The given message must non-null. This
   * message is not sent to the Slack channel.
   *
   * @param msg the error message to log.
   */
  public void info(String msg) {
    this.info(msg, false);
  }

  /**
   * Log the given info-level message to the standard logger. The given message must non-null.
   *
   * @param msg the error message to log.
   * @param sendSlack whether or not to log this particular message to slack.
   */
  public void info(String msg, boolean sendSlack) {
    if (msg == null) {
      String errorMsg = "Given `null` message to log";
      log4jLogger.error(errorMsg);
      sendSlack(errorMsg, false);
      return;
    }

    this.classLogger.info(msg);

    if (sendSlack) {
      sendSlack(msg, false);
    }
  }

  /**
   * Log the given error-level message to the standard logger and to Slack. The given message must
   * be non-null.
   *
   * @param msg the error message to log.
   */
  public void error(String msg) {
    if (msg == null) {
      String errorMsg = "Given `null` message to log";
      log4jLogger.error(errorMsg);
      sendSlack(errorMsg, true);
      return;
    }

    this.classLogger.error(msg);
    sendSlack(msg, true);
  }

  /**
   * Log the given error-level message to the standard logger and to Slack. The exception message
   * and stack trace are appended to the the logged message. The given message and exception must be
   * non-null.
   *
   * @param msg the error message to log.
   * @param e the exception to include in the error message.
   */
  public void error(String msg, Throwable e) {
    if (msg == null || e == null) {
      String errorMsg = String.format("Given `null` message or exception to log: %s, %s", msg, e);
      log4jLogger.error(errorMsg);
      sendSlack(errorMsg, true);
      return;
    }

    this.classLogger.atError().withThrowable(e).log(msg);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String strStackTrace = sw.toString();

    String fullMsg = String.format("%s\n\n ```%s```", msg, strStackTrace);

    sendSlack(fullMsg, true);
  }

  /**
   * Send a Slack message to log an application-level error. This function should not be called
   * anywhere outside of a Vertx error handler.
   */
  public static void logApplicationError(Throwable t) {
    log4jLogger.atError().withThrowable(t).log("Unhandled exception occurred");

    if (!slackEnabled || webhookUrl == null || webClient == null) {
      log4jLogger.info(
          String.format("Not sending Slack message with application error: '%s'", t.getMessage()));
      return;
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    String strStackTrace = sw.toString();

    String fullMsg =
        String.format(
            "`[%s]` `[%s]` *Unhandled exception occurred*:\n\n```%s```\n\n```%s```",
            productName, t.getClass().getSimpleName(), t.getMessage(), strStackTrace);

    SlackRequest request = new SlackRequest(fullMsg);
    JsonObject jsonBody = JsonObject.mapFrom(request);

    webClient
        .postAbs(webhookUrl)
        .putHeader("Content-Type", "application/json")
        .sendJsonObject(jsonBody, SLogger::slackResponseHandler);
  }

  /**
   * Sends the given message to the pre-configured Slack channel. The message will be prefixed with
   * the product name and calling class, and suffixed with the class/line number that called the log
   * message.
   *
   * @param message the message to send.
   * @param atChannel whether or not to `@channel`.
   */
  private void sendSlack(String message, boolean atChannel) {
    if (!slackEnabled || webhookUrl == null || webClient == null) {
      log4jLogger.info(String.format("Not sending given message to Slack: '%s'", message));
      return;
    }

    String fullMessage = this.getFullMessageForSlack(message, atChannel);
    SlackRequest request = new SlackRequest(fullMessage);
    JsonObject jsonBody = JsonObject.mapFrom(request);

    webClient
        .postAbs(webhookUrl)
        .putHeader("Content-Type", "application/json")
        .sendJsonObject(jsonBody, SLogger::slackResponseHandler);
  }

  /**
   * Returns the given message, prefixed with the product name and calling class, and suffixed with
   * the class/line number that called the log message
   *
   * @param message the message to add the prefix and suffix to.
   * @param atChannel whether or not to include `@channel`.
   * @return the full message.
   */
  private String getFullMessageForSlack(String message, boolean atChannel) {
    StringBuilder builder = new StringBuilder();

    if (atChannel) {
      builder.append("<!channel> ");
    }

    builder.append(
        String.format("`[%s]` `[%s]` %s", productName, this.classLogger.getName(), message));

    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (stackTrace.length > 3) {
      builder.append(String.format("\n\n_%s_", stackTrace[3].toString()));
    }

    return builder.toString();
  }

  private static void slackResponseHandler(AsyncResult<HttpResponse<Buffer>> asyncResult) {
    if (!asyncResult.succeeded()) {
      log4jLogger.error("Failed to send Slack message!");
    } else if (asyncResult.result().statusCode() != 200) {
      HttpResponse<Buffer> result = asyncResult.result();
      log4jLogger.error(
          String.format(
              "Error sending Slack message; received a [%d] status response with body: %s",
              result.statusCode(), result.bodyAsString()));
    }
  }

  /**
   * Register this logger for a specific C4C Product. This method should only be called once.
   *
   * @param vertx the Vertx instance used to create a web client for making HTTP requests.
   * @param productName the name of this product used to prefix all Slack messages.
   */
  public static void initializeLogger(Vertx vertx, String productName) {
    webClient = WebClient.create(vertx);
    SLogger.productName = productName;
  }

  /** Represents an HTTP request to send a Slack message. */
  private static class SlackRequest {
    private String text;

    private SlackRequest(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }
  }
}

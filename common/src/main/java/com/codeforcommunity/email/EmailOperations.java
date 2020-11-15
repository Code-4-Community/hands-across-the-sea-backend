package com.codeforcommunity.email;

import com.codeforcommunity.logger.SLogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailOperations {
  private final SLogger logger = new SLogger(EmailOperations.class);

  private final boolean shouldSendEmails;
  private final String senderName;
  private final String sendEmail;
  private final Mailer mailer;

  public EmailOperations(
      boolean shouldSendEmails,
      String senderName,
      String sendEmail,
      String sendPassword,
      String emailHost,
      int emailPort) {
    this.shouldSendEmails = shouldSendEmails;
    this.senderName = senderName;
    this.sendEmail = sendEmail;
    this.mailer =
        MailerBuilder.withSMTPServer(emailHost, emailPort, sendEmail, sendPassword)
            .withTransportStrategy(TransportStrategy.SMTPS)
            .async()
            .buildMailer();
  }

  /**
   * Read the given email template into a string replacing any placeholder strings with their value
   * in the given map.
   *
   * <p>A placeholder string is any string in a {@code ${...}} block. It is expected that the
   * wrapped string is a key in the given map. The entire {@code ${...}} block will be replaced by
   * the key's value in the map.
   *
   * <p>If an exception is encountered while reading the file {@code Optional.empty()} will be
   * returned
   */
  public Optional<String> getTemplateString(
      String templateFilePath, Map<String, String> tagValues) {
    InputStream templateFile;
    try {
      templateFile = EmailOperations.class.getResourceAsStream(templateFilePath);
    } catch (NullPointerException e) {
      logger.error(
          String.format("Could not find the specified email template at `%s`", templateFilePath),
          e);
      return Optional.empty();
    }

    if (templateFile == null) {
      logger.error(
          String.format("Could not find the specified email template at `%s`", templateFilePath));
      return Optional.empty();
    }

    InputStreamReader fr = new InputStreamReader(templateFile);
    boolean readingTag = false;
    StringBuilder output = new StringBuilder();
    StringBuilder tag = new StringBuilder();

    try {
      int next = fr.read();
      while (next != -1) {
        char c = (char) next;
        if (readingTag) {
          if (c == '}') {
            output.append(tagValues.getOrDefault(tag.toString(), tag.toString()));
            readingTag = false;
            tag = new StringBuilder();
          } else {
            tag.append(c);
          }
          next = fr.read();
        } else {
          if (c == '$') {
            next = fr.read();
            if (next == '{') {
              readingTag = true;
              next = fr.read();
            } else {
              output.append('$');
            }
          } else {
            output.append(c);
            next = fr.read();
          }
        }
      }
    } catch (IOException e) {
      logger.error(
          String.format("IOException thrown while reading template file at `%s`", templateFilePath),
          e);
      return Optional.empty();
    }

    return Optional.of(output.toString());
  }

  /**
   * Send an email with the given subject and body to the user with the given name at the given
   * email.
   */
  public void sendEmail(String sendToName, String sendToEmail, String subject, String emailBody) {
    if (!shouldSendEmails) {
      return;
    }

    logger.info(String.format("Sending email with subject `%s`", subject));

    Email email =
        EmailBuilder.startingBlank()
            .from(senderName, sendEmail)
            .to(sendToName, sendToEmail)
            .withSubject(subject)
            .withHTMLText(emailBody)
            .buildEmail();

    try {
      AsyncResponse mailResponse = mailer.sendMail(email, true);

      if (mailResponse == null) {
        logger.error("No mail response returned after trying to send email with subject `%s`");
        return;
      }

      mailResponse.onException(
          (e) -> {
            logger.error(
                String.format("Exception thrown while sending email with subject `%s`", subject),
                e);
          });

      mailResponse.onSuccess(
          () -> {
            logger.info(String.format("Successfully sent email subject `%s`", subject));
          });

    } catch (MailException e) {
      logger.error(
          String.format("`MailException` thrown while sending email with subject `%s`", subject),
          e);
    }
  }
}

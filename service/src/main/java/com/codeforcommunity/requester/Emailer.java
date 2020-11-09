package com.codeforcommunity.requester;

import com.codeforcommunity.email.EmailOperations;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class Emailer {
  private final EmailOperations emailOperations;
  private final String loginUrl;
  private final String passwordResetTemplate;

  public Emailer() {
    Properties emailProperties = PropertiesLoader.getEmailerProperties();
    String senderName = emailProperties.getProperty("senderName");
    String sendEmail = emailProperties.getProperty("sendEmail");
    String sendPassword = emailProperties.getProperty("sendPassword");
    String emailHost = emailProperties.getProperty("emailHost");
    int emailPort = Integer.parseInt(emailProperties.getProperty("emailPort"));
    boolean shouldSendEmails =
        Boolean.parseBoolean(emailProperties.getProperty("shouldSendEmails", "false"));

    this.emailOperations =
        new EmailOperations(
            shouldSendEmails, senderName, sendEmail, sendPassword, emailHost, emailPort);

    Properties frontendProperties = PropertiesLoader.getFrontendProperties();
    this.loginUrl = frontendProperties.getProperty("base_url");
    this.passwordResetTemplate =
        frontendProperties.getProperty("base_url")
            + frontendProperties.getProperty("password_reset_route");
  }

  public void sendWelcomeEmail(String sendToEmail, String sendToName) {
    String filePath = "/emails/WelcomeEmail.html";
    String subjectLine = "Welcome to Speak For The Trees Boston";

    Map<String, String> templateValues = new HashMap<>();
    templateValues.put("name", sendToName);
    templateValues.put("link", loginUrl);
    Optional<String> emailBody = emailOperations.getTemplateString(filePath, templateValues);

    emailBody.ifPresent(s -> emailOperations.sendEmail(sendToName, sendToEmail, subjectLine, s));
  }

  public void sendEmailChangeConfirmationEmail(
      String sendToEmail, String sendToName, String newEmail) {
    String filePath = "/emails/EmailChangeConfirmation.html";
    String subjectLine = "Your Speak For The Trees Email has Changed";

    Map<String, String> templateValues = new HashMap<>();
    templateValues.put("new_email", newEmail);
    Optional<String> emailBody = emailOperations.getTemplateString(filePath, templateValues);

    emailBody.ifPresent(s -> emailOperations.sendEmail(sendToName, sendToEmail, subjectLine, s));
  }

  public void sendPasswordChangeRequestEmail(
      String sendToEmail, String sendToName, String passwordResetKey) {
    String filePath = "/emails/PasswordChangeRequest.html";
    String subjectLine = "Reset your Speak For The Trees Password";

    Map<String, String> templateValues = new HashMap<>();
    templateValues.put("link", String.format(passwordResetTemplate, passwordResetKey));
    Optional<String> emailBody = emailOperations.getTemplateString(filePath, templateValues);

    emailBody.ifPresent(s -> emailOperations.sendEmail(sendToName, sendToEmail, subjectLine, s));
  }

  public void sendPasswordChangeConfirmationEmail(String sendToEmail, String sendToName) {
    String filePath = "/emails/PasswordChangeConfirmation.html";
    String subjectLine = "Your Speak For The Trees Password has Changed";

    Map<String, String> templateValues = new HashMap<>();
    Optional<String> emailBody = emailOperations.getTemplateString(filePath, templateValues);

    emailBody.ifPresent(s -> emailOperations.sendEmail(sendToName, sendToEmail, subjectLine, s));
  }

  public void sendAccountDeactivatedEmail(String sendToEmail, String sendToName) {
    String filePath = "/emails/AccountDeactivated.html";
    String subjectLine = "Your Speak For The Trees Account has been Deleted";

    Map<String, String> templateValues = new HashMap<>();
    Optional<String> emailBody = emailOperations.getTemplateString(filePath, templateValues);

    emailBody.ifPresent(s -> emailOperations.sendEmail(sendToName, sendToEmail, subjectLine, s));
  }
}

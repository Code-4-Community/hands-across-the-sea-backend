package com.codeforcommunity.email;

import com.codeforcommunity.logger.Logger;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Emailer {

  private static Emailer emailer;
  private Session session;
  private String user;
  private String password;

  public static Emailer getInstance() {
    if(emailer == null) {
      emailer = new Emailer();
    }
    return emailer;
  }

  private Emailer() {
    Properties pr;
    try {
      pr = PropertiesLoader.getEmailerProperties();
    } catch (Exception e) {
      pr = new Properties();
    }
    this.user = pr.getProperty("user");
    this.password = pr.getProperty("password");
    this.session = Session.getInstance(pr);
  }

  /**
   * Sends a basic email with given text subject and body to each of the given recipients.
   * Will filter out any malformed email address passed before sending email.
   * @param subject String subject of message to send.
   * @param body String body of message to send. This can be HTML or plaintext.
   * @param recipients list of email addresses to send message to.
   */
  public void send(String subject, String body, List<String> recipients) {

    try {
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(user);
      msg.setRecipients(Message.RecipientType.TO,
              parseRecipients(recipients));
      msg.setSubject(subject);
      msg.setSentDate(new Date());
      msg.setText(body);
      Transport.send(msg, user, password);
    } catch (MessagingException mex) {
      Logger.log("send failed, exception: " + mex);
    }
  }


  private Address[] parseRecipients(List<String> recipients) {

      return recipients.stream()
              .map(r -> {
                try {
                  return new InternetAddress(r);
                } catch (AddressException ae) {
                  return null;
                }
              }).filter(a -> a != null).toArray(Address[]::new);
  }
}

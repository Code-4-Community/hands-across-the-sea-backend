package com.codeforcommunity.email;

import com.codeforcommunity.logger.Logger;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class Emailer {

  private static Emailer emailer;
  private Session session;
  private String user;
  private String password;

  public static void main(String args[]) {
    Emailer em = new Emailer();
  }

  public static Emailer getInstance() {
    if(emailer == null) {
      emailer = new Emailer();
    }
    return emailer;
  }

  private Emailer() {
    Properties pr;
    try {
      pr = PropertiesLoader.getProperties(this.getClass()); //what to do here
    } catch (Exception e) {
      pr = new Properties(); //fix this idfk
    }
    this.user = pr.getProperty("user");
    this.password = pr.getProperty("password");
    this.session = Session.getInstance(pr, null);
  }

  /**
   * Sends a basic email with given text subject and body to each of the given recipients.
   * Will filter out any malformed email address passed before sending email.
   * @param subject String subject of message to send.
   * @param body String body of message to send. This can be HTML or plaintext.
   * @param recipients list of email addresses to send message to.
   */
  public void send(String subject, String body, String[] recipients) {

    try {
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(user); //do I also need this?
      msg.setRecipients(Message.RecipientType.TO,
              parseRecipients(recipients));
      msg.setSubject(subject);
      msg.setSentDate(new Date());
      msg.setText(body);
      Transport.send(msg, user, password); //these will read from secrets file
    } catch (MessagingException mex) {
      Logger.log("send failed, exception: " + mex);
    }
  }


  private Address[] parseRecipients(String[] recipients) {

      return Arrays.stream(recipients)
              .map(r -> {
                try {
                  return new InternetAddress(r);
                } catch (AddressException ae) {
                  return null;
                }
              }).filter(a -> a != null).toArray(Address[]::new);
  }
}

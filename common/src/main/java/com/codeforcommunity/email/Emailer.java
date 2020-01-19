package com.codeforcommunity.email;

import com.codeforcommunity.logger.Logger;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class Emailer {

  private Session session;
  private String sender;
  private String password;

  /**
   * Constructor for Emailer class.
   * @param host stmp server that will handle sending this emails, for example "stmp.gmail.com"
   * @param sender email address from which emails will be sent.
   * @param password password of email address.
   */
  public Emailer(String host, String sender, String password) {
    this.sender = sender;
    this.password = password;
    Properties props = System.getProperties();
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
    props.put("mail.debug", "true");
    props.put("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");
    this.session = Session.getInstance(props, null);
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
      msg.setFrom(sender);
      msg.setRecipients(Message.RecipientType.TO,
              parseRecipients(recipients));
      msg.setSubject(subject);
      msg.setSentDate(new Date());
      msg.setText(body);
      Transport.send(msg, sender, password);
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

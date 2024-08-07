package com.example.rentron.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Gmail {

    // Variable Declaration: constants
    private static final String EMAIL_PORT = "587"; // Gmail's SMTP port
    private static final String SMTP_AUTH = "true"; // Simple Mail Transfer Protocol
    private static final String STARTTLS = "true"; // Transport Layer Security
    private static final String EMAIL_HOST = "smtp.gmail.com";

    // Variable Declaration: email Strings
    private String fromEmail; // Sender email address
    private String fromAppPassword; // App password for the sender email address
    private String toEmail; // Receiver email address
    private String emailSubject; // Subject line of the email
    private String emailBody; // Contents of the email

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage;

    /**
     * Empty constructor
     */
    public Gmail() {}

    /**
     * Constructor method
     * @param fromEmail the sender email address
     * @param fromAppPassword the app password for the sender email address
     * @param toEmail the receiver email address
     * @param emailSubject the subject line of the email
     * @param emailBody the contents of the email
     */
    public Gmail(String fromEmail, String fromAppPassword, String toEmail, String emailSubject,
                 String emailBody) {

        // Initialization
        this.fromEmail = fromEmail;
        this.fromAppPassword = fromAppPassword;
        this.toEmail = toEmail;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        this.emailProperties = System.getProperties();

        // Process: setting up the properties of the mail server
        this.emailProperties.put("mail.smtp.port", EMAIL_PORT);
        this.emailProperties.put("mail.smtp.auth", SMTP_AUTH);
        this.emailProperties.put("mail.smtp.starttls.enable", STARTTLS);

        // LOG MESSAGE
        Log.i("Gmail", "Mail server properties have been set!");
    }

    /**
     * This method creates the email message
     * @return the emailMessage
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public MimeMessage createEmailMessage() throws AddressException, MessagingException,
            UnsupportedEncodingException {

        // Initialization: creating new mail session
        this.mailSession = Session.getDefaultInstance(emailProperties, null);
        this.emailMessage = new MimeMessage(mailSession); // new message

        // Process: setting contents of email
        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail)); // sender
        // LOG MESSAGE
        Log.d("Gmail", "fromEmail: " + fromEmail);

        emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // receiver
        // LOG MESSAGE
        Log.d("Gmail", "toEmail: " + toEmail);

        emailMessage.setSubject(emailSubject); // subject line
        // LOG MESSAGE
        Log.d("Gmail", "emailSubject: " + emailSubject);

        emailMessage.setContent(emailBody, "text/html"); // for HTML email
        // LOG MESSAGE
        Log.d("Gmail", "emailBody: " + emailBody);

        Log.i("Gmail", "Email message successfully created!");

        // Output
        return emailMessage;
    }

    /**
     * This method attempts to send the email
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendEmail() throws AddressException, MessagingException {

        // Variable Declaration
        Transport transport = mailSession.getTransport("smtp"); // getting transport for current session

        // LOG MESSAGES - check these if email cannot be sent (error)
        Log.d("Gmail", "emailHost: " + EMAIL_HOST);

        Log.d("Gmail", "fromEmail: " + fromEmail);
        Log.d("Gmail", "sender:" + emailMessage.getFrom());

        Log.d("Gmail", "toEmail: " + toEmail);
        Log.d("Gmail", "recipient:" + emailMessage.getAllRecipients());

        Log.d("Gmail", "fromAppPassword: " + fromAppPassword);

        // Process: connecting to email
        transport.connect(EMAIL_HOST, fromEmail, fromAppPassword);

        // Process: sending email
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());

        transport.close(); // closing transport

        // LOG MESSAGE
        Log.i("Gmail", "Email has been sent successfully!");
    }
}

package org.snipcloud.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SnipcloudMailer {
	private static final boolean DEBUG = false;
	
	private final Session session;
	private final String from;
	
	
	public SnipcloudMailer(String smtpHost, String from) {
		Properties props = new Properties(); 
        props.put("mail.smtp.host", smtpHost); 
        if (DEBUG) { 
            props.put("mail.debug", "true"); 
        }
        session = Session.getDefaultInstance(props);
        this.from = from;
	}
	
	public void send(String to, String subject, String text) throws AddressException, MessagingException {
		MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO,
                                 new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
        System.out.println("Sent mail to " + to);
	}
	
	public void sendVerificationMail(String to, long userId, String token) throws AddressException, MessagingException {
		final String verificationUrl = "https://134.96.235.19/api/user/" + userId + "/verify/" + token;
		final String text = "Please verify your account at " + verificationUrl;
		send(to, "Snipcloud - Account Verification", text);
	}

}

package rw.rura.rums.module.users.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    public record InviteResult(boolean sent, String message) {
        public static InviteResult success(String contactEmail) {
            return new InviteResult(true, "Invite successfully delivered to " + contactEmail);
        }
        public static InviteResult failure(String contactEmail, String reason) {
            return new InviteResult(false, "Failed to deliver invite to " + contactEmail + ": " + reason);
        }
    }

    public InviteResult sendInvite(String contactEmail, String name, String rumsEmail, String tempPassword) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Rwanda Utilities Regulatory Authority"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(contactEmail));
            message.setSubject("Your RUMS Account Has Been Created");
            message.setText(buildBody(name, rumsEmail, tempPassword));
            Transport.send(message);

            log.info("Invite email delivered to {} for RUMS account {}", contactEmail, rumsEmail);
            return InviteResult.success(contactEmail);

        } catch (MessagingException e) {
            log.error("Failed to send invite to {} for RUMS account {}: {}", contactEmail, rumsEmail, e.getMessage());
            return InviteResult.failure(contactEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending invite to {}: {}", contactEmail, e.getMessage());
            return InviteResult.failure(contactEmail, "Unexpected error — " + e.getMessage());
        }
    }

    private String buildBody(String name, String rumsEmail, String tempPassword) {
        return "Hello " + name + ",\n\n"
                + "Your account on the RURA Management System (RUMS) has been created.\n\n"
                + "Use the credentials below to sign in:\n\n"
                + "  Login Email : " + rumsEmail + "\n"
                + "  Password    : " + tempPassword + "\n\n"
                + "Please change your password immediately after your first sign-in.\n\n"
                + "Rwanda Utilities Regulatory Authority\n"
                + "www.rura.rw";
    }
}

package com.fullStack.expenseTracker.services.impls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fullStack.expenseTracker.services.NotificationService;
import com.fullStack.expenseTracker.models.User;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    /**
     * Reads an HTML template from the templates directory and replaces placeholders with actual values.
     * 
     * @param templateName the name of the template file without the .html extension
     * @param placeholders a map of placeholder names to their values
     * @return the processed template as a string
     * @throws IOException if the template file cannot be read
     */
    private String processTemplate(String templateName, java.util.Map<String, String> placeholders) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName + ".html");
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String content = reader.lines().collect(Collectors.joining("\n"));

            // Replace all placeholders with their values
            for (java.util.Map.Entry<String, String> entry : placeholders.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return content;
        }
    }

    @Override
    public void sendUserRegistrationVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "21223203103@cse.bubt.edu.bd";
        String senderName = "Fitrack - Finance Tracker";
        String subject = "Please verify your registration";

        // Prepare template placeholders
        java.util.Map<String, String> placeholders = new java.util.HashMap<>();
        placeholders.put("username", user.getUsername());
        placeholders.put("verificationCode", user.getVerificationCode());

        String content;
        try {
            // Process the template
            content = processTemplate("registration-verification", placeholders);
        } catch (IOException e) {
            log.error("Failed to read email template: {}", e.getMessage());
            // Fallback to simple content if template fails
            content = "Dear " + user.getUsername() + ",<br><br>"
                    + "<p>Thank you for joining us! We are glad to have you on board.</p><br>"
                    + "<p>To complete the sign up process, enter the verification code in your device.</p><br>"
                    + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                    + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                    + "<br>Thank you,<br>"
                    + "Fitrack.";
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Add headers to improve deliverability
        message.addHeader("X-Priority", "1");
        message.addHeader("X-MSMail-Priority", "High");
        message.addHeader("Importance", "High");
        message.addHeader("X-Mailer", "Fitrack Application");

        helper.setText(content, true);

        try {
            javaMailSender.send(message);
            log.info("Registration verification email sent successfully to {}", toAddress);
        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed. Please check your GMAIL_USER and GMAIL_APP_PASSWORD environment variables. " +
                    "For Gmail, you need to use an App Password, not your regular password. " +
                    "See https://support.google.com/accounts/answer/185833 for instructions on creating an App Password.");
            throw new MessagingException("Email authentication failed. Please check your email configuration. " +
                    "If using Gmail, make sure you're using an App Password, not your regular password.", e);
        } catch (MailSendException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("authentication failed") || e.getCause() instanceof AuthenticationFailedException) {
                log.error("Email authentication failed. Please check your GMAIL_USER and GMAIL_APP_PASSWORD environment variables. " +
                        "For Gmail, you need to use an App Password, not your regular password. " +
                        "See https://support.google.com/accounts/answer/185833 for instructions on creating an App Password.");
                throw new MessagingException("Email authentication failed. Please check your email configuration. " +
                        "If using Gmail, make sure you're using an App Password, not your regular password.", e);
            } else {
                log.error("Failed to send email: {}", e.getMessage());
                throw e;
            }
        }
    }


    public void sendForgotPasswordVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "21223203103@cse.bubt.edu.bd";
        String senderName = "Fitrack";
        String subject = "Forgot password - Please verify your Account";

        // Prepare template placeholders
        java.util.Map<String, String> placeholders = new java.util.HashMap<>();
        placeholders.put("username", user.getUsername());
        placeholders.put("verificationCode", user.getVerificationCode());

        String content;
        try {
            // Process the template
            content = processTemplate("password-reset", placeholders);
        } catch (IOException e) {
            log.error("Failed to read email template: {}", e.getMessage());
            // Fallback to simple content if template fails
            content = "Dear " + user.getUsername() + ",<br><br>"
                    + "<p>To change your password, enter the verification code in your device.</p><br>"
                    + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                    + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                    + "<br>Thank you,<br>"
                    + "Fitrack.";
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Add headers to improve deliverability
        message.addHeader("X-Priority", "1");
        message.addHeader("X-MSMail-Priority", "High");
        message.addHeader("Importance", "High");
        message.addHeader("X-Mailer", "Fitrack Application");

        helper.setText(content, true);

        try {
            javaMailSender.send(message);
            log.info("Password reset verification email sent successfully to {}", toAddress);
        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed. Please check your GMAIL_USER and GMAIL_APP_PASSWORD environment variables. " +
                    "For Gmail, you need to use an App Password, not your regular password. " +
                    "See https://support.google.com/accounts/answer/185833 for instructions on creating an App Password.");
            throw new MessagingException("Email authentication failed. Please check your email configuration. " +
                    "If using Gmail, make sure you're using an App Password, not your regular password.", e);
        } catch (MailSendException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("authentication failed") || e.getCause() instanceof AuthenticationFailedException) {
                log.error("Email authentication failed. Please check your GMAIL_USER and GMAIL_APP_PASSWORD environment variables. " +
                        "For Gmail, you need to use an App Password, not your regular password. " +
                        "See https://support.google.com/accounts/answer/185833 for instructions on creating an App Password.");
                throw new MessagingException("Email authentication failed. Please check your email configuration. " +
                        "If using Gmail, make sure you're using an App Password, not your regular password.", e);
            } else {
                log.error("Failed to send email: {}", e.getMessage());
                throw e;
            }
        }
    }
}

package kg.zhaparov.personal_account.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendVerificationEmail(String email, String otp) {
        String subject = "Email verification";
        String body = "your verification code: " + otp;
        emailService.sendEmail(email, subject, body);
    }
}

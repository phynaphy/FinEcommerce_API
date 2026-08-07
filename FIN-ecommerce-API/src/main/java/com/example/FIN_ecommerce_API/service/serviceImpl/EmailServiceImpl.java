package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP Code");
        message.setText("Your OTP code for resetting your password is: " + otp + "\n\nThis code is valid for 10 minutes.");
        mailSender.send(message);
    }
}
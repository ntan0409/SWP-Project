package com.example.Child.Growth.Tracking.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\n\n" +
                       "This OTP will expire in 5 minutes.\n" +
                       "If you didn't request this, please ignore this email.");

        mailSender.send(message);
    }
}

package com.example.seriesrecommend.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendConfirm(String to,String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("kirillandmax21@gmail.com");
            message.setSubject("Confirm your email");
            String messageBody = """
                    
                    
                    Thank you for registration. Please confirm your email.
                    
                    http://localhost:8080/registration/confirm_token?token=%s
                    
                    """.formatted(token);
            message.setText(messageBody);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendResetPassword(String to,String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("kirillandmax21@gmail.com");
            message.setSubject("Confirm your email");
            String messageBody = """
                    
                    
                    There is your link for reset password. Don`t click if you didn`t want it.
                    
                    http://localhost:8080/reset_password/new?token=%s
                    
                    """.formatted(token);
            message.setText(messageBody);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.noda.api.services;

import com.noda.api.exceptions.IncorrectPasswordException;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.OneTimePassword;
import com.noda.api.repositories.OneTimePasswordRepository;
import com.noda.api.repositories.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Service
public class AuthenticationService {

    private final OneTimePasswordRepository otpRepository;
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();
    private final JavaMailSender mailSender;

    public AuthenticationService(OneTimePasswordRepository otpRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public String authenticateUserAndGenerateOtp(String email, String password) {
        return userRepository.findByEmail(email)
                .map(storedUser -> {
                    if (!storedUser.getPassword().equals(password)) {
                        throw new IncorrectPasswordException("Authentication failed: Incorrect password");
                    }
                    return generateAndSaveOtp(email);
                })
                .orElseThrow(() -> new UserNotFoundException("Authentication failed: User not found"));
    }


    public String generateAndSaveOtp(String email) {
        int number = random.nextInt(900000) + 100000; // maximum 999.999
        String code = String.valueOf(number);

        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        // immutable
        OneTimePassword otp = OneTimePassword.builder()
                .email(email)
                .otpCode(code)
                .expiryTime(expiry)
                .build(); // .build() gathers the data and creates the actual object

        otpRepository.save(otp);

        sendOtpEmail(email, code);

        return code;
    }

    public boolean validateOtp(String email, String userEnteredCode) {
        return otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .map(storedOtp -> {

                    boolean isCodeCorrect = storedOtp.getOtpCode().equals(userEnteredCode);
                    boolean isNotExpired = LocalDateTime.now().isBefore(storedOtp.getExpiryTime());

                    return isCodeCorrect && isNotExpired;
                })
                .orElse(false);
    }
    private void sendOtpEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Login Verification Code");
        message.setText("Hello,\n\nYour 2FA verification code is: " + code + "\n\nThis code expires in 5 minutes.");

        mailSender.send(message);
    }
}

package com.sepdrive.service;

import com.sepdrive.model.User;
import com.sepdrive.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class AuthenService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SecretKey jwtSecret;

    //1. login, dann code senden
    public boolean login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return false;
        }

        String code = generateCode();
        user.setTwoFactorCode(code);
        user.setTwoFactorExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        send2FACodeEmail(user.getEmail(), code);
        return true;

    }
    // step 2 : verifikation durch 2FA oder Super-Code
    public Optional<String> verifyCode(String username, String codeInput) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) { return Optional.empty(); }

        User user = userOpt.get();
        boolean isSuper= codeInput.equals(user.getSuperCode());
        boolean is2FA = codeInput.equals(user.getTwoFactorCode())
                && user.getTwoFactorExpiry()!=null
                && LocalDateTime.now().isBefore(user.getTwoFactorExpiry());
        if(isSuper || is2FA) {
            //code dann ungueltig machen
            user.setTwoFactorCode(null);
            user.setTwoFactorExpiry(null);
            userRepository.save(user);

            //token erstellen
            String token = generateToken(user);
            return Optional.of(token);
        }
        return Optional.empty();

    }


    //--------------Helper Methods--------------
    //1.Token erstellen
    private String generateToken(User user){
        long expirationTime = 3600000;
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())         // hier id als eigener Claim/joseph
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtSecret)
                .compact();
    }
    //2. code random generieren -6 stellen-
    private String generateCode(){
        int code = (int) (Math.random() * 900_000)+100_000;
        return String.valueOf(code);
    }
    //3.email generieren
    private void send2FACodeEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("your 2FA code");
        message.setText("your Code is: " + code);
        mailSender.send(message);
    }





}

package com.sepdrive.controller;

import com.sepdrive.service.AuthenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private AuthenService authenService;

    //login endpoint
    //1. login durch password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        boolean ok = authenService.login(username, password);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
        return ResponseEntity.ok(Map.of("message", "2FA code sent to your email"));
    }
    //2. login durch 2fa oder super
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String code = req.get("code");

        Optional<String> jwtOpt = authenService.verifyCode(username, code);
        if(jwtOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("token", jwtOpt.get()));
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid code or code expired"));

        }
    }
}


package com.krushi.controller;

import com.krushi.dto.OtpRequest;
import com.krushi.dto.OtpVerifyRequest;
import com.krushi.dto.AuthResponse;
import com.krushi.repository.UserRepository;
import com.krushi.entity.UserEntity;
import com.krushi.security.JwtUtil;
import com.krushi.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class OtpController {

    private final OtpService otpService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public OtpController(OtpService otpService, UserRepository userRepo, JwtUtil jwtUtil) {
        this.otpService = otpService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest r){
        if(r == null || r.username == null) return ResponseEntity.badRequest().body("username required");
        String code = otpService.generateAndStore(r.username);
        // In production: send SMS via Twilio / gateway; here we log it
        return ResponseEntity.ok("OTP requested (check server logs for code in dev)");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest req){
        boolean ok = otpService.verify(req.username, req.code);
        if(!ok) return ResponseEntity.status(401).body(new AuthResponse(null, "invalid"));
        // create or upsert user
        var userOpt = userRepo.findByUsername(req.username);
        UserEntity user;
        if(userOpt.isEmpty()){
            user = new UserEntity();
            user.setUsername(req.username);
            user.setPasswordHash(""); // no password for OTP users
            userRepo.save(user);
        } else {
            user = userOpt.get();
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, "ok"));
    }
}

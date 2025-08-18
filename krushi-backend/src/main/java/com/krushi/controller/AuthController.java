package com.krushi.controller;

import com.krushi.dto.AuthRequest;
import com.krushi.dto.AuthResponse;
import com.krushi.entity.UserEntity;
import com.krushi.repository.UserRepository;
import com.krushi.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest req){
        if(userRepo.findByUsername(req.username).isPresent()){
            return ResponseEntity.badRequest().body(new AuthResponse(null, "User already exists"));
        }
        var user = new UserEntity(req.username, passwordEncoder.encode(req.password));
        userRepo.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, "ok"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req){
        var uOpt = userRepo.findByUsername(req.username);
        if(uOpt.isEmpty()) return ResponseEntity.status(401).body(new AuthResponse(null,"invalid"));
        var user = uOpt.get();
        if(!passwordEncoder.matches(req.password, user.getPasswordHash())){
            return ResponseEntity.status(401).body(new AuthResponse(null,"invalid"));
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, "ok"));
    }
}

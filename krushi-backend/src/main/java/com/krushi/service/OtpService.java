package com.krushi.service;

import com.krushi.entity.OtpEntity;
import com.krushi.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.Duration;
import java.util.Random;

@Service
public class OtpService {
    private final OtpRepository repo;
    private final Random rnd = new Random();
    private final long ttlSeconds;

    public OtpService(OtpRepository repo, @Value("${OTP_TTL_SECONDS:300}") long ttlSeconds) {
        this.repo = repo;
        this.ttlSeconds = ttlSeconds;
    }

    public String generateAndStore(String username) {
        // generate 6-digit numeric OTP
        int code = 100000 + rnd.nextInt(900000);
        String sCode = String.valueOf(code);
        // remove old OTPs
        repo.deleteByUsername(username);
        Instant expires = Instant.now().plusSeconds(ttlSeconds);
        OtpEntity e = new OtpEntity(username, sCode, expires);
        repo.save(e);
        // For dev: log it (simulate SMS)
        System.out.printf("OTP for %s -> %s (expires %s)%n", username, sCode, expires.toString());
        return sCode;
    }

    public boolean verify(String username, String code) {
        var opt = repo.findTopByUsernameOrderByCreatedAtDesc(username);
        if(opt.isEmpty()) return false;
        var e = opt.get();
        if(e.getExpiresAt().isBefore(Instant.now())) return false;
        if(!e.getCode().equals(code)) return false;
        // delete OTP after success
        repo.deleteByUsername(username);
        return true;
    }
}

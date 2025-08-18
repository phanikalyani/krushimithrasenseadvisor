package com.krushi.repository;
import com.krushi.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, java.util.UUID> {
    Optional<OtpEntity> findTopByUsernameOrderByCreatedAtDesc(String username);
    void deleteByUsername(String username);
}

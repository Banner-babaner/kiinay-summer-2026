package dev.vorstu.repository;

import dev.vorstu.entity.InviteApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteApplicationRepository extends JpaRepository<InviteApplication, Long> {
    List<String> findEmailByEmailIn(List<String> emails);
    List<String> findLoginByLoginIn(List<String> logins);
    @Query("SELECT ia FROM InviteApplication ia "+
            "WHERE ia.secretKey = :secretKey "+
            "AND ia.used = false "+
            "AND :currentTime >= ia.createdAt "+
            "AND :currentTime < ia.expiresAt")
    Optional<InviteApplication> findActiveBySecretKey(UUID secretKey, LocalDateTime currentTime);
    List<String> findByLoginInAndUsed(List<String> logins, boolean used);
}

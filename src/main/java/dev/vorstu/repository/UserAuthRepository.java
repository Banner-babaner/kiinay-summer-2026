package dev.vorstu.repository;

import dev.vorstu.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    Optional<UserAuth> findByLogin(String login);
    boolean existsByLogin(String login);
}

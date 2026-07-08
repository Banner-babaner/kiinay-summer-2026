package dev.vorstu.repository;

import dev.vorstu.entity.InviteApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteApplicationRepository extends JpaRepository<InviteApplication, Long> {
    List<String> findEmailByEmailIn(List<String> emails);
    List<String> findLoginByLoginIn(List<String> logins);
}

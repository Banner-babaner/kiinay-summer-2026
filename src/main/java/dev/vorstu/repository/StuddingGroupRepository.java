package dev.vorstu.repository;

import dev.vorstu.entity.StuddingGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StuddingGroupRepository extends JpaRepository<StuddingGroup, Long> {
    Optional<StuddingGroup> findByName(String name);
}

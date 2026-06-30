package dev.vorstu.repository;

import dev.vorstu.entity.StuddingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StuddingGroupRepository extends JpaRepository<StuddingGroup, Long> {
    Optional<StuddingGroup> findByName(String name);

    Page<StuddingGroup> findByTeachersId(Long teacherId, Pageable pageable);

    Optional<StuddingGroup> findByIdAndTeachersId(Long groupId, Long teacherId);
}

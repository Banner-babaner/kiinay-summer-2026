package dev.vorstu.repository;

import dev.vorstu.entity.StuddingGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StuddingGroupRepository extends JpaRepository<StuddingGroup, Long> {
    Optional<StuddingGroup> findByName(String name);

    boolean existsByName(String name);

    Page<StuddingGroup> findByTeachersUserAuthId(Long teacherAuthId, Pageable pageable);

    Page<StuddingGroup> findByTeachersId(Long teacherId, Pageable pageable);

    Optional<StuddingGroup> findByIdAndTeachersId(Long groupId, Long teacherId);

    Optional<StuddingGroup> findByIdAndTeachersUserAuthId(Long groupId, Long teacherId);

}

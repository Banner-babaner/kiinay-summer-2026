package dev.vorstu.repository;

import dev.vorstu.entity.Student;
import dev.vorstu.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserAuthId(Long authId);
}

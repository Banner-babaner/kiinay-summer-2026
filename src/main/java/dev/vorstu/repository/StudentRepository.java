package dev.vorstu.repository;

import dev.vorstu.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.group.name = :groupName")
    Page<Student> findByGroup(@Param("groupName") String groupName, Pageable pageable);
    Optional<Student> findByUserAuthId(Long authId);
}

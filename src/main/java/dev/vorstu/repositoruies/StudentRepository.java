package dev.vorstu.repositoruies;

import dev.vorstu.dto.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findByGroup(String group, Pageable pageable);
}

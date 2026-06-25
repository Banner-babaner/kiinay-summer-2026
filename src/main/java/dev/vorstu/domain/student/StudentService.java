package dev.vorstu.domain.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Page<Student> getAllStudents(Pageable pageable);
    Page<Student> getStudentsInGroup(String groupName, Pageable pageable);
    Student getStudent(Long id);
    Long deleteStudent(Long id);
    Student editStudent(
            Long id,
            String fio,
            String group,
            String phoneNumber
    );

    Student createStudent(String fio,
                          String group,
                          String phoneNumber
    );
}

package dev.vorstu.application.student.port.input;

import dev.vorstu.application.student.dto.output.StudentInfo;
import dev.vorstu.domain.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Page<StudentInfo> getAllStudents(Pageable pageable);
    Page<StudentInfo> getStudentsInGroup(String groupName, Pageable pageable);
    StudentInfo getStudent(Long id);
    Long deleteStudent(Long id);
    StudentInfo editStudent(
            Long id,
            String fio,
            String group,
            String phoneNumber
    );

    StudentInfo createStudent(String fio,
                          String group,
                          String phoneNumber
    );
}

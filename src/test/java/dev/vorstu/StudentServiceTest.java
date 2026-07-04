package dev.vorstu;

import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.Student;
import dev.vorstu.mapper.StudentMapper;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.UserAuthRepository;
import dev.vorstu.service.AuthService;
import dev.vorstu.service.StuddingGroupService;
import dev.vorstu.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private StudentMapper mapper;

    @Mock
    private AuthService authService;

    @Mock
    private StuddingGroupService groupService;


    @InjectMocks
    private StudentService studentService;

    @Test
    void gettingInfoAboutExistingStudent() {
        // Студент
        Long studentId = 1L;
        String studentFio = "Иванов Иван";
        Student student = new Student();
        student.setId(studentId);
        student.setFio(studentFio);

        // ДТО
        StudentInfo studentInfo = StudentInfo.builder()
                .id(studentId)
                .fio(studentFio)
                .build();

        // Заглушки на репо и маппер
        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        when(mapper.toStudentInfo(student))
                .thenReturn(studentInfo);

        // Получаем студента
        StudentInfo result = studentService.getStudent(studentId);

        // Проверка, что это тот самый чел
        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Иванов Иван");
    }
}

package dev.vorstu;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.ContactData;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.student.StudentAlreadyHasAccountException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.student.UnknownStudentException;
import dev.vorstu.mapper.StudentMapper;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.UserAuthRepository;
import dev.vorstu.service.AuthService;
import dev.vorstu.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private StudentService studentService;

    @Test
    void getStudentShouldReturnStudentInfoWhenExists() {
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(studentId)
                .fio("Ivanov Ivan")
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        StudentInfo result = studentService.getStudent(studentId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(studentId);
        assertThat(result.getFio()).isEqualTo("Ivanov Ivan");
    }

    @Test
    void getStudentShouldThrowExceptionWhenNotFound() {
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void getAllStudentsShouldReturnPageOfStudents() {
        Pageable pageable = Pageable.unpaged();
        Student student = new Student();
        student.setId(1L);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Ivanov Ivan")
                .build();

        Page<Student> studentPage = new PageImpl<>(List.of(student));
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        Page<StudentInfo> result = studentService.getAllStudents(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFio()).isEqualTo("Ivanov Ivan");
    }

    @Test
    void getStudentsInGroupShouldReturnPageOfStudents() {
        String groupName = "GroupA";
        Pageable pageable = Pageable.unpaged();
        Student student = new Student();
        student.setId(1L);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Ivanov Ivan")
                .build();

        Page<Student> studentPage = new PageImpl<>(List.of(student));
        when(studentRepository.findByGroup(groupName, pageable)).thenReturn(studentPage);
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        Page<StudentInfo> result = studentService.getStudentsInGroup(groupName, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void deleteStudentShouldReturnIdWhenExists() {
        Long studentId = 1L;

        when(studentRepository.existsById(studentId)).thenReturn(true);

        Long result = studentService.deleteStudent(studentId);

        assertThat(result).isEqualTo(studentId);
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void deleteStudentShouldThrowExceptionWhenNotFound() {
        Long studentId = 1L;

        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void editStudentShouldUpdateAndReturnStudentInfo() {
        Long studentId = 1L;
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFio("Petrov Petr");
        request.setPhoneNumber("+71234567890");
        request.setEmail("petrov@mail.ru");

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setFio("Ivanov Ivan");
        existingStudent.setContacts(new ContactData("+79876543210", "ivanov@mail.ru"));

        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setFio("Petrov Petr");
        updatedStudent.setContacts(new ContactData("+71234567890", "petrov@mail.ru"));

        StudentInfo studentInfo = StudentInfo.builder()
                .id(studentId)
                .fio("Petrov Petr")
                .build();

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(studentRepository.getReferenceById(studentId)).thenReturn(existingStudent);
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);
        when(mapper.toStudentInfo(updatedStudent)).thenReturn(studentInfo);

        StudentInfo result = studentService.editStudent(studentId, request);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Petrov Petr");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void editStudentShouldThrowExceptionWhenNotFound() {
        Long studentId = 1L;
        CreateStudentRequest request = new CreateStudentRequest();

        when(studentRepository.existsById(studentId)).thenReturn(false);

        assertThatThrownBy(() -> studentService.editStudent(studentId, request))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    void createStudentShouldReturnStudentInfo() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFio("Sidorov Sidor");
        request.setPhoneNumber("+79998887766");
        request.setEmail("sidorov@mail.ru");

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setFio("Sidorov Sidor");
        savedStudent.setContacts(new ContactData("+79998887766", "sidorov@mail.ru"));

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Sidorov Sidor")
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(mapper.toStudentInfo(savedStudent)).thenReturn(studentInfo);

        StudentInfo result = studentService.createStudent(request);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Sidorov Sidor");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudentWithAuthShouldReturnStudentInfo() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFio("Sidorov Sidor");
        request.setPhoneNumber("+79998887766");
        request.setEmail("sidorov@mail.ru");

        String login = "sidorov";
        String password = "password";

        UserAuth userAuth = UserAuth.builder()
                .login(login)
                .password(password)
                .role(UserRole.STUDENT)
                .build();

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setFio("Sidorov Sidor");
        savedStudent.setContacts(new ContactData("+79998887766", "sidorov@mail.ru"));
        savedStudent.setUserAuth(userAuth);

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Sidorov Sidor")
                .build();

        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);
        when(mapper.toStudentInfo(savedStudent)).thenReturn(studentInfo);

        StudentInfo result = studentService.createStudent(request, login, password);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Sidorov Sidor");
        verify(userAuthRepository).save(any(UserAuth.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudentAccountShouldReturnAuthResponse() {
        Long studentId = 1L;
        String login = "student";
        String password = "pass";

        Student student = new Student();
        student.setId(studentId);
        student.setUserAuth(null);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("token")
                .build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(authService.register(any(SignUpRequest.class), eq(student))).thenReturn(authResponse);

        AuthResponse result = studentService.createStudentAccount(studentId, login, password);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("token");
        verify(authService).register(any(SignUpRequest.class), eq(student));
    }

    @Test
    void createStudentAccountShouldThrowExceptionWhenStudentNotFound() {
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.createStudentAccount(studentId, "login", "pass"))
                .isInstanceOf(StudentNotFoundException.class);

        verify(authService, never()).register(any(), any());
    }

    @Test
    void createStudentAccountShouldThrowExceptionWhenStudentAlreadyHasAccount() {
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setUserAuth(new UserAuth());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> studentService.createStudentAccount(studentId, "login", "pass"))
                .isInstanceOf(StudentAlreadyHasAccountException.class);

        verify(authService, never()).register(any(), any());
    }

    @Test
    void getByAuthIdShouldReturnStudentInfo() {
        Long authId = 1L;
        Student student = new Student();
        student.setId(1L);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Ivanov Ivan")
                .build();

        when(studentRepository.findByUserAuthId(authId)).thenReturn(Optional.of(student));
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        StudentInfo result = studentService.getByAuthId(authId);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Ivanov Ivan");
    }

    @Test
    void getByAuthIdShouldThrowExceptionWhenNotFound() {
        Long authId = 1L;

        when(studentRepository.findByUserAuthId(authId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getByAuthId(authId))
                .isInstanceOf(UnknownStudentException.class);
    }

    @Test
    void getTeachingStudentShouldReturnStudentInfo() {
        Long studentId = 1L;
        Long teacherId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(studentId)
                .fio("Ivanov Ivan")
                .build();

        when(studentRepository.findByIdAndTeacherIdWithGroup(studentId, teacherId))
                .thenReturn(Optional.of(student));
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        StudentInfo result = studentService.getTeachingStudent(studentId, teacherId);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Ivanov Ivan");
    }

    @Test
    void getTeachingStudentShouldThrowExceptionWhenNotFound() {
        Long studentId = 1L;
        Long teacherId = 1L;

        when(studentRepository.findByIdAndTeacherIdWithGroup(studentId, teacherId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getTeachingStudent(studentId, teacherId))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void getTeachingStudentAuthedShouldReturnStudentInfo() {
        Long studentId = 1L;
        Long teacherAuthId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setFio("Ivanov Ivan");

        StudentInfo studentInfo = StudentInfo.builder()
                .id(studentId)
                .fio("Ivanov Ivan")
                .build();

        when(studentRepository.findByIdAndTeacherUserAuthIdWithGroup(studentId, teacherAuthId))
                .thenReturn(Optional.of(student));
        when(mapper.toStudentInfo(student)).thenReturn(studentInfo);

        StudentInfo result = studentService.getTeachingStudentAuthed(studentId, teacherAuthId);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Ivanov Ivan");
    }

    @Test
    void getTeachingStudentAuthedShouldThrowExceptionWhenNotFound() {
        Long studentId = 1L;
        Long teacherAuthId = 1L;

        when(studentRepository.findByIdAndTeacherUserAuthIdWithGroup(studentId, teacherAuthId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getTeachingStudentAuthed(studentId, teacherAuthId))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void deleteStudentAccountShouldRemoveAuthWhenExists() {
        Long studentId = 1L;
        UserAuth auth = new UserAuth();
        auth.setId(1L);

        Student student = new Student();
        student.setId(studentId);
        student.setUserAuth(auth);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        studentService.deleteStudentAccount(studentId);

        assertThat(student.getUserAuth()).isNull();
        verify(userAuthRepository).delete(auth);
    }

    @Test
    void deleteStudentAccountShouldDoNothingWhenNoAuth() {
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setUserAuth(null);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        studentService.deleteStudentAccount(studentId);

        verify(userAuthRepository, never()).delete(any());
    }

    @Test
    void deleteStudentAccountShouldThrowExceptionWhenStudentNotFound() {
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudentAccount(studentId))
                .isInstanceOf(StudentNotFoundException.class);

        verify(userAuthRepository, never()).delete(any());
    }
}
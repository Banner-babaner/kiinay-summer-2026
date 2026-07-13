package dev.vorstu;

import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.ContactData;
import dev.vorstu.entity.Teacher;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.student.UnknownStudentException;
import dev.vorstu.exception.teacher.TeacherAlreadyHasAccountException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import dev.vorstu.mapper.TeacherMapper;
import dev.vorstu.repository.TeacherRepository;
import dev.vorstu.repository.UserAuthRepository;
import dev.vorstu.service.AuthService;
import dev.vorstu.service.TeacherService;
import dev.vorstu.validator.FioValidator;
import dev.vorstu.validator.PhoneValidator;
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
public class TeacherServiceTest {
    @Mock
    private TeacherRepository repository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private TeacherMapper mapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void getTeacherByIdShouldReturnTeacherInfo() {
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFio("Petrov Petr");

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(teacherId)
                .fio("Petrov Petr")
                .build();

        when(repository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(mapper.toTeacherInfo(teacher)).thenReturn(teacherInfo);

        TeacherInfo result = teacherService.getTeacherById(teacherId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(teacherId);
        assertThat(result.getFio()).isEqualTo("Petrov Petr");
    }

    @Test
    void getTeacherByIdShouldThrowExceptionWhenNotFound() {
        Long teacherId = 1L;

        when(repository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.getTeacherById(teacherId))
                .isInstanceOf(TeacherNotFoundException.class);
    }

    @Test
    void getAllTeachersShouldReturnPageOfTeachers() {
        Pageable pageable = Pageable.unpaged();
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFio("Petrov Petr");

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(1L)
                .fio("Petrov Petr")
                .build();

        Page<Teacher> teacherPage = new PageImpl<>(List.of(teacher));
        when(repository.findAll(pageable)).thenReturn(teacherPage);
        when(mapper.toTeacherInfo(teacher)).thenReturn(teacherInfo);

        Page<TeacherInfo> result = teacherService.getAllTeachers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFio()).isEqualTo("Petrov Petr");
    }

    @Test
    void deleteTeacherShouldReturnIdWhenExists() {
        Long teacherId = 1L;

        when(repository.existsById(teacherId)).thenReturn(true);

        long result = teacherService.deleteTeacher(teacherId);

        assertThat(result).isEqualTo(teacherId);
        verify(repository).deleteById(teacherId);
    }

    @Test
    void deleteTeacherShouldThrowExceptionWhenNotFound() {
        Long teacherId = 1L;

        when(repository.existsById(teacherId)).thenReturn(false);

        assertThatThrownBy(() -> teacherService.deleteTeacher(teacherId))
                .isInstanceOf(NullPointerException.class);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void editTeacherShouldUpdateAndReturnTeacherInfo() {
        Long teacherId = 1L;
        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setFio("Sidorov Sidor");
        request.setPhoneNumber("+71234567890");
        request.setEmail("sidorov@mail.ru");

        Teacher existingTeacher = new Teacher();
        existingTeacher.setId(teacherId);
        existingTeacher.setFio("Petrov Petr");
        existingTeacher.setContacts(new ContactData("+79876543210", "petrov@mail.ru"));

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(teacherId);
        updatedTeacher.setFio("Sidorov Sidor");
        updatedTeacher.setContacts(new ContactData("+71234567890", "sidorov@mail.ru"));

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(teacherId)
                .fio("Sidorov Sidor")
                .build();

        when(repository.findById(teacherId)).thenReturn(Optional.of(existingTeacher));
        when(repository.save(existingTeacher)).thenReturn(updatedTeacher);
        when(mapper.toTeacherInfo(updatedTeacher)).thenReturn(teacherInfo);

        TeacherInfo result = teacherService.editTeacher(teacherId, request);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Sidorov Sidor");
        verify(repository).save(existingTeacher);
    }

    @Test
    void editTeacherShouldThrowExceptionWhenNotFound() {
        Long teacherId = 1L;
        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setPhoneNumber("+71234567890");
        request.setFio("Sidorov Sidor");

        when(repository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.editTeacher(teacherId, request))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void createTeacherShouldReturnTeacherInfo() {
        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setFio("Sidorov Sidor");
        request.setPhoneNumber("+79998887766");
        request.setEmail("sidorov@mail.ru");

        Teacher savedTeacher = new Teacher();
        savedTeacher.setId(1L);
        savedTeacher.setFio("Sidorov Sidor");
        savedTeacher.setContacts(new ContactData("+79998887766", "sidorov@mail.ru"));

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(1L)
                .fio("Sidorov Sidor")
                .build();

        when(repository.save(any(Teacher.class))).thenReturn(savedTeacher);
        when(mapper.toTeacherInfo(savedTeacher)).thenReturn(teacherInfo);

        TeacherInfo result = teacherService.createTeacher(request);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Sidorov Sidor");
        verify(repository).save(any(Teacher.class));
    }

    @Test
    void createTeacherWithAuthShouldReturnTeacherInfo() {
        CreateTeacherRequest request = new CreateTeacherRequest();
        request.setFio("Sidorov Sidor");
        request.setPhoneNumber("+79998887766");
        request.setEmail("sidorov@mail.ru");

        String login = "sidorov";
        String password = "password";

        UserAuth userAuth = UserAuth.builder()
                .login(login)
                .password(password)
                .role(UserRole.TEACHER)
                .build();

        Teacher savedTeacher = new Teacher();
        savedTeacher.setId(1L);
        savedTeacher.setFio("Sidorov Sidor");
        savedTeacher.setContacts(new ContactData("+79998887766", "sidorov@mail.ru"));
        savedTeacher.setUserAuth(userAuth);

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(1L)
                .fio("Sidorov Sidor")
                .build();

        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);
        when(repository.save(any(Teacher.class))).thenReturn(savedTeacher);
        when(mapper.toTeacherInfo(savedTeacher)).thenReturn(teacherInfo);

        TeacherInfo result = teacherService.createTeacher(request, login, password);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Sidorov Sidor");
        verify(userAuthRepository).save(any(UserAuth.class));
        verify(repository).save(any(Teacher.class));
    }

    @Test
    void createTeacherAccountShouldReturnAuthResponse() {
        Long teacherId = 1L;
        String login = "teacher";
        String password = "pass";

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setUserAuth(null);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("token")
                .build();

        when(repository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(authService.register(any(SignUpRequest.class), eq(teacher))).thenReturn(authResponse);

        AuthResponse result = teacherService.createTeacherAccount(teacherId, login, password);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("token");
        verify(authService).register(any(SignUpRequest.class), eq(teacher));
    }

    @Test
    void createTeacherAccountShouldThrowExceptionWhenTeacherNotFound() {
        Long teacherId = 1L;

        when(repository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.createTeacherAccount(teacherId, "login", "pass"))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(authService, never()).register(any(), any());
    }

    @Test
    void createTeacherAccountShouldThrowExceptionWhenTeacherAlreadyHasAccount() {
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setUserAuth(new UserAuth());

        when(repository.findById(teacherId)).thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> teacherService.createTeacherAccount(teacherId, "login", "pass"))
                .isInstanceOf(TeacherAlreadyHasAccountException.class);

        verify(authService, never()).register(any(), any());
    }

    @Test
    void getByAuthIdShouldReturnTeacherInfo() {
        Long authId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFio("Petrov Petr");

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(1L)
                .fio("Petrov Petr")
                .build();

        when(repository.findByUserAuthId(authId)).thenReturn(Optional.of(teacher));
        when(mapper.toTeacherInfo(teacher)).thenReturn(teacherInfo);

        TeacherInfo result = teacherService.getByAuthId(authId);

        assertThat(result).isNotNull();
        assertThat(result.getFio()).isEqualTo("Petrov Petr");
    }

    @Test
    void getByAuthIdShouldThrowExceptionWhenNotFound() {
        Long authId = 1L;

        when(repository.findByUserAuthId(authId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.getByAuthId(authId))
                .isInstanceOf(UnknownStudentException.class);
    }

    @Test
    void deleteTeacherAccountShouldRemoveAuthWhenExists() {
        Long teacherId = 1L;
        UserAuth auth = new UserAuth();
        auth.setId(1L);

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setUserAuth(auth);

        when(repository.findById(teacherId)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacherAccount(teacherId);

        assertThat(teacher.getUserAuth()).isNull();
        verify(userAuthRepository).delete(auth);
    }

    @Test
    void deleteTeacherAccountShouldDoNothingWhenNoAuth() {
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setUserAuth(null);

        when(repository.findById(teacherId)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacherAccount(teacherId);

        verify(userAuthRepository, never()).delete(any());
    }

    @Test
    void deleteTeacherAccountShouldThrowExceptionWhenTeacherNotFound() {
        Long teacherId = 1L;

        when(repository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.deleteTeacherAccount(teacherId))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(userAuthRepository, never()).delete(any());
    }
}
package dev.vorstu;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.CreateUserCsv;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.InviteApplication;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.invite.DuplicateLoginException;
import dev.vorstu.exception.invite.IllegalInviteRoleException;
import dev.vorstu.exception.invite.InviteApplicationNotFoundException;
import dev.vorstu.parser.CsvParser;
import dev.vorstu.repository.InviteApplicationRepository;
import dev.vorstu.service.*;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InviteApplicationServiceTest {
    @Mock
    private Validator validator;

    @Mock
    private InviteApplicationRepository repository;

    @Mock
    private CsvParser parser;

    @Mock
    private StuddingGroupService groupService;

    @Mock
    private AuthService authService;

    @Mock
    private MailService mailService;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private InviteApplicationService inviteApplicationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(inviteApplicationService, "inviteMessage", "Welcome!");
        ReflectionTestUtils.setField(inviteApplicationService, "endpoint", "http://localhost/invite/");
        ReflectionTestUtils.setField(inviteApplicationService, "liveSeconds", 3600L);
    }

    @Test
    void inviteFromCsvShouldProcessValidCsv() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        CreateUserCsv userCsv = CreateUserCsv.builder()
                .fio("Ivanov Ivan")
                .login("ivanov")
                .password("pass")
                .role(UserRole.STUDENT)
                .email("ivanov@mail.ru")
                .phoneNumber("+71234567890")
                .groupName("GroupA")
                .build();

        List<CreateUserCsv> sources = List.of(userCsv);

        when(parser.parseCsv(file, CreateUserCsv.class)).thenReturn(sources);
        doNothing().when(authService).checkDuplicateLogins(any());
        when(repository.findByLoginInAndUsed(any(), eq(true))).thenReturn(List.of());
        when(repository.saveAll(any())).thenReturn(List.of());
        doNothing().when(mailService).sendSimpleMessage(anyString(), anyString(), anyString());

        inviteApplicationService.inviteFromCsv(file);

        verify(parser).parseCsv(file, CreateUserCsv.class);
        verify(authService).checkDuplicateLogins(any());
        verify(repository).findByLoginInAndUsed(any(), eq(true));
        verify(repository).saveAll(any());
        verify(mailService, atLeastOnce()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void inviteFromCsvShouldThrowExceptionWhenParsingFails() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        when(parser.parseCsv(file, CreateUserCsv.class)).thenThrow(new RuntimeException("Parse error"));

        assertThatThrownBy(() -> inviteApplicationService.inviteFromCsv(file))
                .isInstanceOf(RuntimeException.class);

        verify(repository, never()).saveAll(any());
        verify(mailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void inviteFromCsvShouldThrowExceptionWhenDuplicateLogins() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        CreateUserCsv userCsv = CreateUserCsv.builder()
                .login("ivanov")
                .build();

        List<CreateUserCsv> sources = List.of(userCsv);

        when(parser.parseCsv(file, CreateUserCsv.class)).thenReturn(sources);
        doThrow(new DuplicateLoginException(List.of("ivanov"))).when(authService).checkDuplicateLogins(any());

        assertThatThrownBy(() -> inviteApplicationService.inviteFromCsv(file))
                .isInstanceOf(DuplicateLoginException.class);

        verify(repository, never()).saveAll(any());
        verify(mailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void inviteFromCsvShouldThrowExceptionWhenLoginsAlreadyUsed() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        CreateUserCsv userCsv = CreateUserCsv.builder()
                .login("ivanov")
                .build();

        List<CreateUserCsv> sources = List.of(userCsv);

        when(parser.parseCsv(file, CreateUserCsv.class)).thenReturn(sources);
        doNothing().when(authService).checkDuplicateLogins(any());
        when(repository.findByLoginInAndUsed(any(), eq(true))).thenReturn(List.of("ivanov"));

        assertThatThrownBy(() -> inviteApplicationService.inviteFromCsv(file))
                .isInstanceOf(DuplicateLoginException.class);

        verify(repository, never()).saveAll(any());
        verify(mailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void useSecretKeyShouldCreateStudentAndAddToGroup() {
        String secretKey = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(secretKey);

        InviteApplication application = InviteApplication.builder()
                .secretKey(uuid)
                .fio("Ivanov Ivan")
                .login("ivanov")
                .password("pass")
                .role(UserRole.STUDENT)
                .email("ivanov@mail.ru")
                .phoneNumber("+71234567890")
                .groupName("GroupA")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Ivanov Ivan")
                .build();



        when(groupService.findOrCreateByName("GroupA")).thenReturn(GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build());
        when(studentService.createStudent(any(CreateStudentRequest.class), anyString(), anyString()))
                .thenReturn(studentInfo);
        when(repository.findActiveBySecretKey(any(UUID.class), any(LocalDateTime.class))).thenReturn(Optional.of(application));
        doNothing().when(groupService).addStudent(1L, 1L);

        inviteApplicationService.useSecretKey(secretKey);

        assertThat(application.getUsed()).isTrue();
        verify(studentService).createStudent(any(CreateStudentRequest.class), eq("ivanov"), eq("pass"));
        verify(groupService).addStudent(1L, 1L);
    }

    @Test
    void useSecretKeyShouldCreateStudentWithoutGroup() {
        String secretKey = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(secretKey);

        InviteApplication application = InviteApplication.builder()
                .secretKey(uuid)
                .fio("Ivanov Ivan")
                .login("ivanov")
                .password("pass")
                .role(UserRole.STUDENT)
                .email("ivanov@mail.ru")
                .phoneNumber("+71234567890")
                .groupName(null)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        StudentInfo studentInfo = StudentInfo.builder()
                .id(1L)
                .fio("Ivanov Ivan")
                .build();

        when(repository.findActiveBySecretKey(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(application));
        when(studentService.createStudent(any(CreateStudentRequest.class), anyString(), anyString()))
                .thenReturn(studentInfo);

        inviteApplicationService.useSecretKey(secretKey);

        assertThat(application.getUsed()).isTrue();
        verify(studentService).createStudent(any(CreateStudentRequest.class), eq("ivanov"), eq("pass"));
        verify(groupService, never()).addStudent(anyLong(), anyLong());
    }

    @Test
    void useSecretKeyShouldCreateTeacherAndAddToGroup() {
        String secretKey = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(secretKey);

        InviteApplication application = InviteApplication.builder()
                .secretKey(uuid)
                .fio("Petrov Petr")
                .login("petrov")
                .password("pass")
                .role(UserRole.TEACHER)
                .email("petrov@mail.ru")
                .phoneNumber("+79876543210")
                .groupName("GroupA")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .id(1L)
                .fio("Petrov Petr")
                .build();

        when(repository.findActiveBySecretKey(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(application));
        when(groupService.findOrCreateByName("GroupA")).thenReturn(GroupInfo.builder()
                .name("GroupA")
                .id(1L)
                .build());
        when(teacherService.createTeacher(any(CreateTeacherRequest.class), anyString(), anyString()))
                .thenReturn(teacherInfo);
        doNothing().when(groupService).addTeacher(1L, 1L);

        inviteApplicationService.useSecretKey(secretKey);

        assertThat(application.getUsed()).isTrue();
        verify(teacherService).createTeacher(any(CreateTeacherRequest.class), eq("petrov"), eq("pass"));
        verify(groupService).addTeacher(1L, 1L);
    }

    @Test
    void useSecretKeyShouldThrowExceptionWhenNotFound() {
        String secretKey = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(secretKey);

        when(repository.findActiveBySecretKey(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> inviteApplicationService.useSecretKey(secretKey))
                .isInstanceOf(InviteApplicationNotFoundException.class);
    }

    @Test
    void useSecretKeyShouldThrowExceptionWhenInvalidRole() {
        String secretKey = UUID.randomUUID().toString();
        UUID uuid = UUID.fromString(secretKey);

        InviteApplication application = InviteApplication.builder()
                .secretKey(uuid)
                .fio("User")
                .login("user")
                .password("pass")
                .role(null)
                .email("user@mail.ru")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        when(repository.findActiveBySecretKey(any(UUID.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() -> inviteApplicationService.useSecretKey(secretKey))
                .isInstanceOfAny(IllegalInviteRoleException.class, NullPointerException.class);

        verify(studentService, never()).createStudent(any(), anyString(), anyString());
        verify(teacherService, never()).createTeacher(any(), anyString(), anyString());
    }

    @Test
    void useSecretKeyShouldThrowExceptionWhenInvalidSecretKeyFormat() {
        String invalidSecretKey = "invalid-uuid";

        assertThatThrownBy(() -> inviteApplicationService.useSecretKey(invalidSecretKey))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).findActiveBySecretKey(any(), any());
    }

    @Test
    void inviteFromCsvShouldSendEmailForEachInvite() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        CreateUserCsv userCsv1 = CreateUserCsv.builder()
                .fio("Ivanov Ivan")
                .login("ivanov")
                .password("pass")
                .role(UserRole.STUDENT)
                .email("ivanov@mail.ru")
                .phoneNumber("+71234567890")
                .groupName("GroupA")
                .build();

        CreateUserCsv userCsv2 = CreateUserCsv.builder()
                .fio("Petrov Petr")
                .login("petrov")
                .password("pass")
                .role(UserRole.TEACHER)
                .email("petrov@mail.ru")
                .phoneNumber("+79876543210")
                .groupName("GroupB")
                .build();

        List<CreateUserCsv> sources = List.of(userCsv1, userCsv2);

        when(parser.parseCsv(file, CreateUserCsv.class)).thenReturn(sources);
        doNothing().when(authService).checkDuplicateLogins(any());
        when(repository.findByLoginInAndUsed(any(), eq(true))).thenReturn(List.of());
        when(repository.saveAll(any())).thenReturn(List.of());
        doNothing().when(mailService).sendSimpleMessage(anyString(), anyString(), anyString());

        inviteApplicationService.inviteFromCsv(file);

        verify(mailService, times(2)).sendSimpleMessage(anyString(), eq("INVITE"), anyString());
    }

    @Test
    void inviteFromCsvShouldContinueSendingEmailsWhenOneFails() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());

        CreateUserCsv userCsv1 = CreateUserCsv.builder()
                .fio("Ivanov Ivan")
                .login("ivanov")
                .password("pass")
                .role(UserRole.STUDENT)
                .email("ivanov@mail.ru")
                .build();

        CreateUserCsv userCsv2 = CreateUserCsv.builder()
                .fio("Petrov Petr")
                .login("petrov")
                .password("pass")
                .role(UserRole.TEACHER)
                .email("petrov@mail.ru")
                .build();

        List<CreateUserCsv> sources = List.of(userCsv1, userCsv2);

        when(parser.parseCsv(file, CreateUserCsv.class)).thenReturn(sources);
        doNothing().when(authService).checkDuplicateLogins(any());
        when(repository.findByLoginInAndUsed(any(), eq(true))).thenReturn(List.of());
        when(repository.saveAll(any())).thenReturn(List.of());
        doThrow(new RuntimeException("Email failed")).when(mailService).sendSimpleMessage(anyString(), anyString(), anyString());

        inviteApplicationService.inviteFromCsv(file);

        verify(mailService, atLeast(2)).sendSimpleMessage(anyString(), anyString(), anyString());
    }
}
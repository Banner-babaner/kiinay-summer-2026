package dev.vorstu.service;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.CreateUserCsv;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.InviteApplication;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Teacher;
import dev.vorstu.exception.invite.DuplicateLoginException;
import dev.vorstu.exception.invite.IllegalInviteRoleException;
import dev.vorstu.parser.CsvParser;
import dev.vorstu.repository.InviteApplicationRepository;
import dev.vorstu.exception.invite.InviteApplicationNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteApplicationService {
    private final Validator validator;
    private final InviteApplicationRepository repository;
    private final CsvParser parser;
    private final StuddingGroupService groupService;
    private final AuthService authService;
    private final MailService mailService;
    private final StudentService studentService;
    private final TeacherService teacherService;


    @Value("${app.invite.message:}")
    private String inviteMessage;

    @Value("${app.invite.endpoint}")
    private String endpoint;

    @Value("${app.invite.live-seconds:3600}")
    private Long liveSeconds;

    @Transactional
    public void inviteFromCsv(@NonNull MultipartFile file){
        List<CreateUserCsv> sources;
        log.info("Start parsing");
        try {
            sources = parser.parseCsv(file, CreateUserCsv.class);
            log.info("Success parsed");
        } catch (Exception e) {
            log.error("Can not read file {}", file.getOriginalFilename(), e);
            throw new RuntimeException(e);
        }

        List<String> logins = sources.stream().map(CreateUserCsv::toString).toList();
        checkBookedLogins(logins);

        List<InviteApplication> toSave = sources.stream().map(this::createFromRequest).toList();
        log.info("Batching save {} invites", toSave.size());
        try{
            repository.saveAll(toSave);
        }
        catch (Exception e){
            log.warn("Invites were not saved", e);
            throw new RuntimeException(e);
        }
        log.info("Invites were successfully saved");
        sendInvites(toSave);
    }

    @Transactional
    public void useSecretKey(String secretKey){
        log.info("Trying use secret key {}", secretKey);
        UUID uuid = UUID.fromString(secretKey);
        InviteApplication application = repository.findActiveBySecretKey(uuid, LocalDateTime.now())
                .orElseThrow(() -> new InviteApplicationNotFoundException("Not found active invites where secretKey = " + secretKey));

        StuddingGroup group = application.getGroupName() == null ? null :
                StuddingGroup.builder()
                        .id(groupService.findOrCreateByName(application.getGroupName()).getId())
                        .build();

        switch (application.getRole()) {
            case STUDENT -> {
                StudentInfo studentInfo = studentService.createStudent(CreateStudentRequest.builder()
                                .fio(application.getFio())
                                .email(application.getEmail())
                                .phoneNumber(application.getPhoneNumber()).build(),
                        application.getLogin(),
                        application.getPassword());
                if (group != null)
                    groupService.addStudent(studentInfo.getId(), group.getId());
            }
            case TEACHER -> {
                TeacherInfo teacher = teacherService.createTeacher(CreateTeacherRequest.builder()
                                .fio(application.getFio())
                                .email(application.getEmail())
                                .phoneNumber(application.getPhoneNumber())
                                .build(),
                        application.getLogin(),
                        application.getPassword());
                if (group != null)
                    groupService.addTeacher(teacher.getId(), group.getId());
            }
            default -> throw new IllegalInviteRoleException(application.getRole().name());
        }
        application.setUsed(true);

    }



    private void checkBookedLogins(List<String> logins){
        authService.checkDuplicateLogins(logins);

        List<String> duplicates = repository.findByLoginInAndUsed(logins, true);
        if(!duplicates.isEmpty())
            throw new DuplicateLoginException(logins);
    }

    private InviteApplication createFromRequest(CreateUserCsv request){
        InviteApplication application = InviteApplication.builder()
                .groupName(request.getGroupName())
                .fio(request.getFio())
                .login(request.getLogin())
                .role(request.getRole())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber()==null||request.getPhoneNumber().isBlank()?null:
                        request.getPhoneNumber())
                .email(request.getEmail())
                .expiresAt(LocalDateTime.now().plusSeconds(liveSeconds))
                .build();
        validator.validate(application);
        return application;
    }

    private void sendInvites(List<InviteApplication> invites){
        invites.forEach(invite->
                {
                    try {
                        mailService.sendSimpleMessage(
                                invite.getEmail(),
                                "INVITE",
                                inviteMessage + "\n" + endpoint + invite.getSecretKey());
                    } catch (Exception e) {
                        log.warn("Can not send email for {}", invite, e);
                    }
                }
        );
    }


}

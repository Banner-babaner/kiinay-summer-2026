package dev.vorstu.service;

import dev.vorstu.dto.input.CreateUserCsv;
import dev.vorstu.entity.InviteApplication;
import dev.vorstu.parser.CsvParser;
import dev.vorstu.repository.InviteApplicationRepository;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteApplicationService {
    private final Validator validator;
    private final InviteApplicationRepository repository;
    private final CsvParser parser;
    private final StuddingGroupService groupService;
    private final AuthService authService;

    @Value("${app.invite.live-seconds:3600}")
    private Long liveSeconds;

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

    }

    private InviteApplication createApplication(CreateUserCsv request){
        validator.validate(request);
        LocalDateTime now = LocalDateTime.now();
        return InviteApplication.builder()
                .fio(request.getFio())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .groupName(request.getGroupName())
                .createdAt(now)
                .expiresAt(now.plusSeconds(liveSeconds))
                .build();
    }

    private void checkBookedLogin(CreateUserCsv csv){

    }
}

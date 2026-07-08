package dev.vorstu.controller.admin;

import dev.vorstu.service.InviteApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/invites")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminInviteController {
    private final InviteApplicationService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> inviteFromCsv(@RequestParam("file")
                                                  @NotNull MultipartFile file){
        service.inviteFromCsv(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

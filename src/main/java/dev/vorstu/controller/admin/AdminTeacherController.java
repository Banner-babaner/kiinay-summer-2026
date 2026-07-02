package dev.vorstu.controller.admin;

import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.service.TeacherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/teachers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminTeacherController {
    private final TeacherService teacherService;

    @GetMapping
    public Page<TeacherInfo> getAllTeachers(
            @ParameterObject
            Pageable pageable){
        return teacherService.getAllTeachers(pageable);
    }

    @GetMapping("/{id}")
    public TeacherInfo getTeacherById(Long teacherId){
        return teacherService.getTeacherById(teacherId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherInfo> editTeacherById(@PathVariable("id") Long teacherId,
                                       @Valid @RequestBody CreateTeacherRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.editTeacher(teacherId, request));
    }

    @DeleteMapping("/{id}")
    public Long deleteTeacherById(@PathVariable("id") Long teacherId){
        return teacherService.deleteTeacher(teacherId);
    }

    @PostMapping
    public ResponseEntity<TeacherInfo> createTeacher(
            @Valid @RequestBody CreateTeacherRequest request
    ) {
      return ResponseEntity.status(HttpStatus.CREATED).body(
              teacherService.createTeacher(request)
      );
    }

    @PostMapping("/{id}/account")
    public ResponseEntity<AuthResponse> createTeacherAccount(
            @PathVariable("id") Long teacherId,
            String login,
            String password
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.createTeacherAccount(teacherId,
                        login,
                        password));
    }

    @DeleteMapping("/{id}/account")
    public void deleteAccountIfExists(@PathVariable("id") Long teacherId){
        teacherService.deleteTeacherAccount(teacherId);
    }
}

package dev.vorstu.controller.teacher;

import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import dev.vorstu.service.TeacherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('TEACHER')")
@RequestMapping("/api/teacher/students")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TeacherStudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;


}

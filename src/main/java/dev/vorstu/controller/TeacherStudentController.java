package dev.vorstu.controller;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('TEACHER')")
@RequestMapping("api/admin/students")
@RequiredArgsConstructor
public class TeacherStudentController {
    private final StudentService studentService;



}

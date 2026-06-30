package dev.vorstu.controller.student;

import dev.vorstu.controller.annotations.CurrentUser;
import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentController {
    private final StudentService studentService;


    @PutMapping("/me")
    public StudentInfo editMe(@CurrentUser Long id, @Valid @RequestBody CreateStudentRequest request){
        return changeStudent(studentService.getByAuthId(id).getId(), request);
    }


    private StudentInfo changeStudent(
            Long studentId,
            CreateStudentRequest request){
        return studentService.editStudent(
                studentId,
                request.getFio(),
                request.getPhoneNumber()
        );
    }
}

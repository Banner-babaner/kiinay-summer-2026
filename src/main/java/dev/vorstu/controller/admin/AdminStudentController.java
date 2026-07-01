package dev.vorstu.controller.admin;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminStudentController {
    private final StudentService studentService;

    @GetMapping(value = "/{id}")
    public StudentInfo getStudentById(@PathVariable("id") Long id){
        return studentService.getStudent(id);
    }

    @PostMapping
    public ResponseEntity<StudentInfo> createStudent(@Valid @RequestBody CreateStudentRequest newStudent){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(
                newStudent.getFio(),
                newStudent.getPhoneNumber()
        ));
    }

    @GetMapping("/filter")
    public Page<StudentInfo> getFilteredStudents(@RequestParam("group") String group,
                                                 Pageable pageable){
        return studentService.getStudentsInGroup(group, pageable);
    }

    @GetMapping
    public Page<StudentInfo> getAllStudents(Pageable pageable){
        return studentService.getAllStudents(pageable);
    }

    @DeleteMapping("/{id}")
    public Long deleteStudent(@PathVariable("id") Long id){
        return studentService.deleteStudent(id);
    }

    @PostMapping("/{id}/account")
    public ResponseEntity<AuthResponse> createStudentAccount(Long studentId, String login, String password){
        return ResponseEntity.status(HttpStatus.CREATED).
                body(studentService.createStudentAccount(studentId, login, password));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentInfo> changeStudent(
            @PathVariable("id") Long studentId,
            @RequestBody CreateStudentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.editStudent(
                studentId,
                request.getFio(),
                request.getPhoneNumber()
        ));
    }
}

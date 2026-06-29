package dev.vorstu.controller;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("api/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {
    private final StudentService studentService;

    @GetMapping(value = "students/{id}")
    public StudentInfo getStudentById(@PathVariable("id") Long id){
        return studentService.getStudent(id);
    }

    @PostMapping
    public StudentInfo createStudent(@Valid @RequestBody CreateStudentRequest newStudent){
        return studentService.createStudent(
                newStudent.getFio(),
                newStudent.getGroupId(),
                newStudent.getPhoneNumber()
        );
    }

    @GetMapping(value = "students/filter")
    public Page<StudentInfo> getFilteredStudents(@RequestParam("group") String group,
                                                 Pageable pageable){
        return studentService.getStudentsInGroup(group, pageable);
    }

    @GetMapping(value="students")
    public Page<StudentInfo> getAllStudents(Pageable pageable){
        return studentService.getAllStudents(pageable);
    }

    @DeleteMapping(value = "{id}")
    public Long deleteStudent(@PathVariable("id") Long id){
        return studentService.deleteStudent(id);
    }

    @PostMapping("{id}/account")
    public AuthResponse createStudentAccount(Long studentId, String login, String password){
        return studentService.createStudentAccount(studentId, login, password);
    }

    @PutMapping(value = "students/{id}")
    public StudentInfo changeStudent(
            @PathVariable("id") Long studentId,
            @RequestBody CreateStudentRequest request){
        return studentService.editStudent(
                studentId,
                request.getFio(),
                request.getGroupId(),
                request.getPhoneNumber()
        );
    }
}

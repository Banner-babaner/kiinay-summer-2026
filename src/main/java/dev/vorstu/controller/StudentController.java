package dev.vorstu.controller;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Date;


@RestController
@RequestMapping("api/base")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;


    @GetMapping("check")
    public String greetJava(){
        return "Hello world "+new Date();
    }

    @GetMapping(value="students", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StudentInfo> getAllStudents(Pageable pageable){
        return studentService.getAllStudents(pageable);
    }

    @GetMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentInfo getStudentById(@PathVariable("id") Long id){
        return studentService.getStudent(id);
    }

    @GetMapping(value = "students/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StudentInfo> getFilteredStudents(@RequestParam("group") String group,
                                             Pageable pageable){
        return studentService.getStudentsInGroup(group, pageable);
    }

    @PostMapping(value = "students", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentInfo createStudent(@Valid @RequestBody StudentInfo newStudent){
        return studentService.createStudent(
                newStudent.getFio(),
                newStudent.getGroup(),
                newStudent.getPhoneNumber()
        );
    }


    @PutMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentInfo changeStudent(
            @PathVariable("id") Long studentId,
            @RequestBody CreateStudentRequest request){
        return studentService.editStudent(
                        studentId,
                        request.getFio(),
                        request.getGroup(),
                        request.getPhoneNumber()
                );
    }

    @DeleteMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long deleteStudent(@PathVariable("id") Long id){
        return studentService.deleteStudent(id);
    }


}

package dev.vorstu.infrastructure.web;

import dev.vorstu.domain.student.Student;
import dev.vorstu.domain.student.StudentService;
import dev.vorstu.infrastructure.dto.requests.CreateStudentRequest;
import dev.vorstu.infrastructure.dto.response.StudentInfo;
import dev.vorstu.infrastructure.mapper.GlobalMapper;
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
    private final GlobalMapper mapper;


    @GetMapping("check")
    public String greetJava(){
        return "Hello world "+new Date();
    }

    @GetMapping(value="students", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StudentInfo> getAllStudents(Pageable pageable){
        return studentService.getAllStudents(pageable).map(mapper::toStudentInfo);
    }

    @GetMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudentById(@PathVariable("id") Long id){
        return studentService.getStudent(id);
    }

    @GetMapping(value = "students/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<StudentInfo> getFilteredStudents(@RequestParam("group") String group,
                                             Pageable pageable){
        return studentService.getStudentsInGroup(group, pageable).map(mapper::toStudentInfo);
    }

    @PostMapping(value = "students", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentInfo createStudent(@Valid @RequestBody StudentInfo newStudent){
        return mapper.toStudentInfo(
                studentService.createStudent(
                newStudent.getFio(),
                newStudent.getGroup(),
                newStudent.getPhoneNumber()
        ));
    }


    @PutMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentInfo changeStudent(
            @PathVariable("id") Long studentId,
            @RequestBody CreateStudentRequest request){
        return mapper.toStudentInfo(
                studentService.editStudent(
                        studentId,
                        request.getFio(),
                        request.getGroup(),
                        request.getPhoneNumber()
                )
        );
    }

    @DeleteMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long deleteStudent(@PathVariable("id") Long id){
        return studentService.deleteStudent(id);
    }


}

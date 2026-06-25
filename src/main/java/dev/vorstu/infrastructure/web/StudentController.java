package dev.vorstu.infrastructure.controller;

import dev.vorstu.domain.student.Student;
import dev.vorstu.repositoruies.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Date;


@RestController
@RequestMapping("api/base")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;//Сервисы покинули чат на время выполнения методички

    @GetMapping("check")
    public String greetJava(){
        return "Hello world "+new Date();
    }

    @GetMapping(value="students", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Student> getAllStudents(Pageable pageable){//Нас учили, возвращаеть списки из бд - плохой тон
        return studentRepository.findAll(pageable);
    }

    @GetMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudentById(@PathVariable("id") Long id){
        return studentRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Student with id: "+id+ " was not found"));//Пока пусть просто рантайм
    }

    @GetMapping(value = "students/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Student> getFilteredStudents(@RequestParam("group") String group,
                                             Pageable pageable){
        return studentRepository.findByGroup(group, pageable);
    }

    @PostMapping(value = "students", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student createStudent(@RequestBody Student newStudent){
        if(newStudent.getId()!=null)
            throw new RuntimeException("Oh no, it is not PUT-method! Id must be null");
        return studentRepository.save(newStudent);
    }


    @PutMapping(value = "students", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student changeStudent(@RequestBody Student changingStudent){
        if(studentRepository.existsById(changingStudent.getId()))
            throw new RuntimeException("Student with id: "+changingStudent.getId()+ " was not found");
        return studentRepository.save(changingStudent);
    }

    @DeleteMapping(value = "students/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long deleteStudent(@PathVariable("id") Long id){
        if(studentRepository.existsById(id))
            throw new RuntimeException("Student with id: "+id+ " was not found");
        studentRepository.deleteById(id);
        return id;
    }


}

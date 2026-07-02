package dev.vorstu;

import static org.junit.jupiter.api.Assertions.*;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.exception.common.InvalidPhoneNumberException;
import dev.vorstu.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class StudentServiceTest {
    @Autowired
    private  StudentService studentService;

    @Test
    void createStudent(){
        log.info("CREATING VALID STUDENT");
        String fio = "Абрикосов Абрикос Абрикосович";
        String phone = "88005553535";
        StudentInfo studentInfo = studentService.
                createStudent(fio, phone);
        assertNotNull(studentInfo);
        log.info("Created student with name {} and phone {}",
                studentInfo.getFio(), studentInfo.getPhoneNumber());
        assertEquals(fio, studentInfo.getFio());
        assertEquals(phone, studentInfo.getPhoneNumber());
    }

    @Test
    void createStudentWithInvalidPhone(){
        log.info("CREATING STUDENT WITH INVALID PHONE");
        String fio = "Абрикосов Абрикос Абрикосович";
        String phone = ">w<";
        assertThrows(InvalidPhoneNumberException.class,
                ()->studentService.createStudent(fio, phone),
                "Must throw InvalidPhoneNumberException");
    }

    @AfterEach
    void logPassed(){
        log.info("TEST PASSED");
    }
}

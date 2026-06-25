package dev.vorstu.infrastructure.initializer;

import dev.vorstu.domain.student.Student;
import dev.vorstu.repositoruies.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {
    @Autowired
    StudentRepository studentRepository;

    private void initial(){
        studentRepository.save(new Student("Vasia Pupkin", "VM", "+7"));
        studentRepository.save(new Student("ShadowFrog", "VM", "+8"));
        studentRepository.save(new Student("Barsik", "AM", "+99"));
    }

    @Override
    public void run(String... args) throws Exception {
        initial();
    }
}

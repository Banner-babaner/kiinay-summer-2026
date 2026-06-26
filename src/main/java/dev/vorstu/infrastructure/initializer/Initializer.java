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
        System.out.println("Здесь могла бы быть иницилизация начальных данных, но не будет");
    }

    @Override
    public void run(String... args) throws Exception {
        initial();
    }
}

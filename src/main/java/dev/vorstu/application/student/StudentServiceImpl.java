package dev.vorstu.application.student;

import dev.vorstu.domain.student.Student;
import dev.vorstu.domain.student.StudentService;
import dev.vorstu.domain.student.exception.*;
import dev.vorstu.repositoruies.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> getStudentsInGroup(String groupName, Pageable pageable) {
        return null;
    }

    @Override
    public Student getStudent(Long id) {
        if(id==null) throw new NullPointerException("id is null");
        return studentRepository.findById(id)
                .orElseThrow(()->new StudentNotFoundException(id.toString()));
    }

    @Override
    public Long deleteStudent(Long id) {
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        studentRepository.deleteById(id);
        return id;
    }

    @Override
    public Student editStudent(
            Long id,
            String fio,
            String group,
            String phoneNumber
    ) {
        if(id==null)
            throw new NullPointerException("id is null");
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        validateFio(fio);
        validateGroupName(group);
        validatePhoneNumber(phoneNumber);
        return studentRepository.save(new Student(id, fio, group, phoneNumber));
    }

    @Override
    public Student createStudent(String fio,
                                 String group,
                                 String phoneNumber) {
       validateFio(fio);
       validateGroupName(group);
       validatePhoneNumber(phoneNumber);
        return studentRepository.save(new Student(fio, group, phoneNumber));
    }

    private void validateFio(String fio){
        if(fio==null)
            throw new NullPointerException("fio is null");
        if(fio.isBlank() || fio.length()>64)
            throw new InvalidFioFormatException(fio);
    }

    private void validateGroupName(String groupName){
        if(groupName!=null && (groupName.isBlank() || groupName.length()>64))
            throw new InvalidGroupNameException(groupName);
    }

    private void validatePhoneNumber(String phoneNumber){
        if(phoneNumber != null && (phoneNumber.isBlank() || phoneNumber.length()>24))
            throw new InvalidPhoneNumberException(phoneNumber);
    }
}

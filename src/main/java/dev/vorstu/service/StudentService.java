package dev.vorstu.service;

import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.student.Student;
import dev.vorstu.exception.student.InvalidFioFormatException;
import dev.vorstu.exception.student.InvalidGroupNameException;
import dev.vorstu.exception.student.InvalidPhoneNumberException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.mapper.StudentMapper;
import dev.vorstu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;

    public Page<StudentInfo> getAllStudents(Pageable pageable){
        return studentRepository.findAll(pageable).map(mapper::toStudentInfo);
    }

    public Page<StudentInfo> getStudentsInGroup(String groupName, Pageable pageable) {
        return studentRepository.findByGroup(groupName, pageable).map(mapper::toStudentInfo);
    }

    public StudentInfo getStudent(Long id) {
        if(id==null) throw new NullPointerException("id is null");
        return mapper.toStudentInfo(studentRepository.findById(id)
                .orElseThrow(()->new StudentNotFoundException(id.toString())));
    }

    public Long deleteStudent(Long id) {
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        studentRepository.deleteById(id);
        return id;
    }

    public StudentInfo editStudent(
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
        Student student = studentRepository.getReferenceById(id);
        student.setFio(fio);
        student.setGroup(group);
        student.setPhoneNumber(phoneNumber);

        return mapper.toStudentInfo(studentRepository.save(student));
    }

    public StudentInfo createStudent(String fio,
                                     String group,
                                     String phoneNumber) {
        validateFio(fio);
        validateGroupName(group);
        validatePhoneNumber(phoneNumber);
        return mapper.toStudentInfo(studentRepository.save(new Student(fio, group, phoneNumber)));
    }

    public void validateFio(String fio){
        if(fio==null)
            throw new NullPointerException("fio is null");
        if(fio.isBlank() || fio.length()>64)
            throw new InvalidFioFormatException(fio);
    }

    public void validateGroupName(String groupName){
        if(groupName!=null && (groupName.isBlank() || groupName.length()>64))
            throw new InvalidGroupNameException(groupName);
    }

    public void validatePhoneNumber(String phoneNumber){
        if(phoneNumber != null && (phoneNumber.isBlank() || phoneNumber.length()>24))
            throw new InvalidPhoneNumberException(phoneNumber);
    }
}

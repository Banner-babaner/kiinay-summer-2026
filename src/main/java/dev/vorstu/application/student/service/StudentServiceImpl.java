package dev.vorstu.application.student.service;

import dev.vorstu.application.student.dto.output.StudentInfo;
import dev.vorstu.application.student.port.input.StudentService;
import dev.vorstu.domain.student.Student;
import dev.vorstu.domain.student.StudentValidator;
import dev.vorstu.domain.student.exception.*;
import dev.vorstu.application.student.mapper.StudentMapper;
import dev.vorstu.repositoruies.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;
    private final StudentValidator validator;

    @Override
    public Page<StudentInfo> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(mapper::toStudentInfo);
    }

    @Override
    public Page<StudentInfo> getStudentsInGroup(String groupName, Pageable pageable) {
        return studentRepository.findByGroup(groupName, pageable).map(mapper::toStudentInfo);
    }

    @Override
    public StudentInfo getStudent(Long id) {
        if(id==null) throw new NullPointerException("id is null");
        return mapper.toStudentInfo(studentRepository.findById(id)
                .orElseThrow(()->new StudentNotFoundException(id.toString())));
    }

    @Override
    public Long deleteStudent(Long id) {
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        studentRepository.deleteById(id);
        return id;
    }

    @Override
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
        validator.validateFio(fio);
        validator.validateGroupName(group);
        validator.validatePhoneNumber(phoneNumber);
        return mapper.toStudentInfo(studentRepository.save(new Student(id, fio, group, phoneNumber)));
    }

    @Override
    public StudentInfo createStudent(String fio,
                                 String group,
                                 String phoneNumber) {
        validator.validateFio(fio);
        validator.validateGroupName(group);
        validator.validatePhoneNumber(phoneNumber);
        return mapper.toStudentInfo(studentRepository.save(new Student(fio, group, phoneNumber)));
    }


}

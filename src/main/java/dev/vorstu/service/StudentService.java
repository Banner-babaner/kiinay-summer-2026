package dev.vorstu.service;

import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.common.InvalidFioFormatException;
import dev.vorstu.exception.common.InvalidPhoneNumberException;
import dev.vorstu.exception.student.*;
import dev.vorstu.exception.teacher.DoesntTeachThisGroupException;
import dev.vorstu.mapper.StudentMapper;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.UserAuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final AuthService authService;
    private final UserAuthRepository userAuthRepository;
    private final StuddingGroupService studdingGroupService;
    private final StudentMapper mapper;


    @Transactional
    public AuthResponse createStudentAccount(Long studentId, String login, String password){
        if(studentId==null) throw new NullPointerException("id is null");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()->new StudentNotFoundException(studentId.toString()));
        if(student.getUserAuth()!=null) throw new StudentAlreadyHasAccountException("id="+studentId);
        AuthResponse response = authService.register(
                SignUpRequest.builder()
                        .login(login)
                        .password(password)
                        .role(UserRole.STUDENT)
                        .build()
        );
        student.setUserAuth(
                userAuthRepository.getReferenceById(response.getAccountId())
        );
        return response;
    }

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

    @Transactional
    public StudentInfo editStudent(
            Long id,
            String fio,
            String phoneNumber
    ) {
        if(id==null)
            throw new NullPointerException("id is null");
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        validateFio(fio);
        validatePhoneNumber(phoneNumber);
        Student student = studentRepository.getReferenceById(id);
        student.setFio(fio);
        student.setPhoneNumber(phoneNumber);

        return mapper.toStudentInfo(studentRepository.save(student));
    }

    public StudentInfo createStudent(String fio,
                                     String phoneNumber) {
        validateFio(fio);
        validatePhoneNumber(phoneNumber);
        Student saved = studentRepository.save(new Student(fio, phoneNumber));

        return mapper.toStudentInfo(saved);
    }

    private void validateFio(String fio){
        if(fio==null)
            throw new NullPointerException("fio is null");
        if(fio.isBlank() || fio.length()>64)
            throw new InvalidFioFormatException(fio);
    }


    private void validatePhoneNumber(String phoneNumber){
        if(phoneNumber != null && (phoneNumber.isBlank() || phoneNumber.length()>24))
            throw new InvalidPhoneNumberException(phoneNumber);
    }

    public StudentInfo getByAuthId(Long authId){
        if(authId==null)
            throw new NullPointerException("authId");
        return mapper.toStudentInfo(studentRepository.findByUserAuthId(authId)
                .orElseThrow(()->new UnknownStudentException("userAuthId="+authId)));
    }

    public StudentInfo getTeachingStudent(Long studentId, Long teacherId){
        return mapper.toStudentInfo(studentRepository.findByIdAndTeacherIdWithGroup(studentId,
                teacherId).orElseThrow(()->new StudentNotFoundException("id="+studentId)));
    }

    public StudentInfo getTeachingStudentAuthed(Long studentId, Long teacherAuthId){
        return mapper.toStudentInfo(studentRepository.findByIdAndTeacherUserAuthIdWithGroup(studentId,
                teacherAuthId).orElseThrow(()->new StudentNotFoundException("id="+studentId)));
    }

}

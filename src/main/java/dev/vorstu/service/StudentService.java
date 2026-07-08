package dev.vorstu.service;

import dev.vorstu.dto.input.CreateStudentRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.entity.ContactData;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.common.InvalidFioFormatException;
import dev.vorstu.exception.common.InvalidPhoneNumberException;
import dev.vorstu.exception.student.*;
import dev.vorstu.exception.teacher.DoesntTeachThisGroupException;
import dev.vorstu.mapper.StudentMapper;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.UserAuthRepository;
import dev.vorstu.validator.FioValidator;
import dev.vorstu.validator.PhoneValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.regex.Matcher;

@Service
@Validated
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final AuthService authService;
    private final UserAuthRepository userAuthRepository;
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
                        .build(),
                student
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
            @Valid CreateStudentRequest request
    ) {
        if(id==null)
            throw new NullPointerException("id is null");
        if(!studentRepository.existsById(id))
            throw new StudentNotFoundException(id.toString());
        Student student = studentRepository.getReferenceById(id);
        student.setFio(request.getFio());
        student.setContacts(ContactData.builder()
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                .build());

        return mapper.toStudentInfo(studentRepository.save(student));
    }

    public StudentInfo createStudent(@Valid CreateStudentRequest request) {
        Student saved = studentRepository.save(Student.builder()
                        .fio(request.getFio())
                        .contacts(new ContactData(request.getPhoneNumber(), request.getEmail()))
                .build());

        return mapper.toStudentInfo(saved);
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

    @Transactional
    public void deleteStudentAccount(Long studentId){
        if(studentId==null)
            throw new NullPointerException("studentId");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()->new StudentNotFoundException("id="+studentId));
        UserAuth auth = student.getUserAuth();
        if(auth==null) return;
        student.setUserAuth(null);
        userAuthRepository.delete(auth);
    }
}

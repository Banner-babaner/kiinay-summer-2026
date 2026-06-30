package dev.vorstu.service;

import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.Teacher;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.student.StudentAlreadyHasAccountException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.student.UnknownStudentException;
import dev.vorstu.exception.teacher.TeacherAlreadyHasAccountException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import dev.vorstu.mapper.TeacherMapper;
import dev.vorstu.repository.TeacherRepository;
import dev.vorstu.repository.UserAuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final AuthService authService;
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    public AuthResponse createTeacherAccount(Long teacherId, String login, String password){
        if(teacherId==null) throw new NullPointerException("id is null");
        Teacher teacher = repository.findById(teacherId)
                .orElseThrow(()->new StudentNotFoundException(teacherId.toString()));
        if(teacher.getUserAuth()!=null) throw new TeacherAlreadyHasAccountException("id="+teacherId);
        AuthResponse response = authService.register(
                SignUpRequest.builder()
                        .login(login)
                        .password(password)
                        .role(UserRole.TEACHER)
                        .build()
        );
        teacher.setUserAuth(
                userAuthRepository.getReferenceById(response.getAccountId())
        );
        return response;
    }

    public TeacherInfo getTeacherById(Long teacherId){
        if(teacherId==null)
            throw new NullPointerException("teacherId");
        return mapper.toTeacherInfo(repository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId)));
    }

    public Page<TeacherInfo> getAllTeachers(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toTeacherInfo);
    }

    public long deleteTeacher(Long teacherId){
        if(teacherId==null)
            throw new NullPointerException("teacherId");
        if(!repository.existsById(teacherId))
            throw new NullPointerException("teacherId");
        repository.deleteById(teacherId);
        return teacherId;
    }

    @Transactional
    public TeacherInfo editTeacher(Long teacherId, CreateTeacherRequest request){
        if(teacherId==null)
            throw new NullPointerException("teacherId");
        Teacher teacher = repository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId));
        teacher.setFio(request.getFio());
        teacher.setPhoneNumber(request.getPhoneNumber());
        return mapper.toTeacherInfo(repository.save(teacher));
    }

    public TeacherInfo getByAuthId(Long authId){
        if(authId==null)
            throw new NullPointerException("authId");
        return mapper.toTeacherInfo(repository.findByUserAuthId(authId)
                .orElseThrow(()->new UnknownStudentException("userAuthId="+authId)));
    }




}

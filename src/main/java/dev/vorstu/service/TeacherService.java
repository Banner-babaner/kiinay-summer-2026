package dev.vorstu.service;

import dev.vorstu.dto.input.CreateTeacherRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.dto.output.TeacherInfo;
import dev.vorstu.entity.ContactData;
import dev.vorstu.entity.Teacher;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.entity.UserRole;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.student.UnknownStudentException;
import dev.vorstu.exception.teacher.TeacherAlreadyHasAccountException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import dev.vorstu.mapper.TeacherMapper;
import dev.vorstu.repository.TeacherRepository;
import dev.vorstu.repository.UserAuthRepository;
import dev.vorstu.validator.FioValidator;
import dev.vorstu.validator.PhoneValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TeacherService {
    private final AuthService authService;
    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    public AuthResponse createTeacherAccount(@NonNull Long teacherId, @NonNull String login, @NonNull String password){
        Teacher teacher = repository.findById(teacherId)
                .orElseThrow(()->new StudentNotFoundException(teacherId.toString()));
        if(teacher.getUserAuth()!=null) throw new TeacherAlreadyHasAccountException("id="+teacherId);
        return authService.register(
                SignUpRequest.builder()
                        .login(login)
                        .password(password)
                        .role(UserRole.TEACHER)
                        .build(),
                teacher
        );
    }

    public TeacherInfo getTeacherById(@NonNull Long teacherId){
        return mapper.toTeacherInfo(repository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId)));
    }

    public Page<TeacherInfo> getAllTeachers(Pageable pageable){
        return repository.findAll(pageable).map(mapper::toTeacherInfo);
    }

    public long deleteTeacher(@NonNull Long teacherId){
        if(!repository.existsById(teacherId))
            throw new NullPointerException("teacherId");
        repository.deleteById(teacherId);
        return teacherId;
    }

    @Transactional
    public TeacherInfo editTeacher(@NonNull Long teacherId, CreateTeacherRequest request){
        PhoneValidator.validate(request.getPhoneNumber());
        FioValidator.validate(request.getFio());
        Teacher teacher = repository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId));
        teacher.setFio(request.getFio());
        teacher.setContacts(ContactData.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build());
        return mapper.toTeacherInfo(repository.save(teacher));
    }

    @Transactional
    public TeacherInfo createTeacher(@Valid @NonNull CreateTeacherRequest request){
        FioValidator.validate(request.getFio());
        PhoneValidator.validate(request.getPhoneNumber());
        Teacher teacher = Teacher.builder()
                .fio(request.getFio())
                .contacts(new ContactData(request.getPhoneNumber(), request.getEmail()))
                .build();
        return mapper.toTeacherInfo(repository.save(teacher));
    }

    @Transactional
    public TeacherInfo createTeacher(@Valid @NonNull CreateTeacherRequest request,
                                     @NonNull String login, @NonNull String password){
        FioValidator.validate(request.getFio());
        PhoneValidator.validate(request.getPhoneNumber());
        Teacher teacher = Teacher.builder()
                .fio(request.getFio())
                .contacts(new ContactData(request.getPhoneNumber(), request.getEmail()))
                .userAuth(userAuthRepository.save(UserAuth.builder().login(login).password(password).role(UserRole.TEACHER).build()))
                .build();
        return mapper.toTeacherInfo(repository.save(teacher));
    }

    public TeacherInfo getByAuthId(@NonNull Long authId){
        return mapper.toTeacherInfo(repository.findByUserAuthId(authId)
                .orElseThrow(()->new UnknownStudentException("userAuthId="+authId)));
    }


    @Transactional
    public void deleteTeacherAccount(@NonNull Long teacherId){
        Teacher teacher = repository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId));
        UserAuth auth = teacher.getUserAuth();
        if(auth==null) return;
        teacher.setUserAuth(null);
        userAuthRepository.delete(auth);
    }





}

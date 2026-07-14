package dev.vorstu.service;

import dev.vorstu.dto.input.CreateGroupRequest;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.Teacher;
import dev.vorstu.exception.group.*;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import dev.vorstu.mapper.GroupMapper;
import dev.vorstu.repository.StuddingGroupRepository;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StuddingGroupService {
    private final StuddingGroupRepository studdingGroupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final GroupMapper mapper;

    @Transactional
    public void addStudent(@NonNull Long studentId, @NonNull Long groupId){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()->new StudentNotFoundException("id="+studentId));
        StuddingGroup group = studdingGroupRepository.findById(groupId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId));
        Set<Student> presented = group.getStudents();
        if(presented.contains(student))
            throw new StudentAlreadyPresentsException("student "+student.getFio()+
                    "group "+group.getName());
        student.setGroup(group);
    }

    @Transactional
    public void addTeacher(@NonNull Long teacherId, @NonNull Long groupId){
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(()->new TeacherNotFoundException("id="+teacherId));
        StuddingGroup group = studdingGroupRepository.findById(groupId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId));
        Set<Teacher> presented = group.getTeachers();
        if(presented.contains(teacher))
            throw new TeacherAlreadyTeachesHereException("teacher "+teacher.getFio()+
                    "group "+group.getName());
        if(teacher.getGroups()==null)
            teacher.setGroups(Set.of(group));
        else
            teacher.getGroups().add(group);
    }

    public GroupInfo getGroup(@NonNull Long id){
        return mapper.toGroupInfo(studdingGroupRepository.findById(id)
                .orElseThrow(()->new GroupNotFoundException("id="+id)));
    }

    public Page<GroupInfo> getTeacherGroups(@NonNull Long teacherId, @NonNull Pageable pageable){
        return studdingGroupRepository.
                findByTeachersId(teacherId, pageable).map(mapper::toGroupInfo);
    }

    public GroupInfo getTeachersGroup(@NonNull Long teacherId, @NonNull Long groupId){
        return mapper.toGroupInfo(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId)));
    }

    public Page<GroupInfo> getTeacherGroupsAuthed(Long teacherUserAuthId, Pageable pageable){
        return studdingGroupRepository.
                findByTeachersUserAuthId(teacherUserAuthId, pageable).map(mapper::toGroupInfo);
    }

    public GroupInfo getTeachersGroupAuthed(Long teacherUserAuthId, Long groupId){
        return mapper.toGroupInfo(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherUserAuthId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId)));
    }

    @Transactional
    public GroupInfo createGroup(CreateGroupRequest request){
        if(studdingGroupRepository.existsByName(request.getName()))
            throw new DuplicateGroupNameException(request.getName());
        return mapper.toGroupInfo(studdingGroupRepository.save(
                StuddingGroup.builder()
                        .name(request.getName())
                        .build()
        ));
    }

    @Transactional
    public GroupInfo editGroup(Long groupId, CreateGroupRequest request){
        if(groupId==null)
            throw new NullPointerException("groupId");
        StuddingGroup toEdit = studdingGroupRepository.findById(groupId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId));
        toEdit.setName(request.getName());
        return mapper.toGroupInfo(studdingGroupRepository.save(toEdit));

    }

    @Transactional
    public Long deleteGroup(Long groupId){
        if(groupId==null)
            throw new NullPointerException("groupId");
        StuddingGroup toDelete= studdingGroupRepository.findById(groupId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId));
        if(!toDelete.getStudents().isEmpty())
            throw new NotEmptyGroupException("id="+groupId);
        studdingGroupRepository.deleteById(groupId);
        return groupId;
    }

    public GroupInfo getStudentGroupAuthed(Long authId){
        StuddingGroup group =  studdingGroupRepository.findByStudentsUserAuthId(authId).orElse(null);
        if(group==null) return null;
        return mapper.toGroupInfo(group);
    }

    public Page<GroupInfo> getAllGroups(Pageable pageable){
        return studdingGroupRepository.findAll(pageable).map(mapper::toGroupInfo);
    }

    public GroupInfo getGroupByName(String groupName){
        return mapper.toGroupInfo(studdingGroupRepository.findByName(groupName)
                .orElseThrow(()->new GroupNotFoundException(groupName)));
    }

    @Transactional
    public GroupInfo findOrCreateByName(String groupName) {
        return mapper.toGroupInfo(studdingGroupRepository.findByName(groupName)
                .orElseGet(()->studdingGroupRepository.save(
                        StuddingGroup.builder().name(groupName).build())));
    }
}

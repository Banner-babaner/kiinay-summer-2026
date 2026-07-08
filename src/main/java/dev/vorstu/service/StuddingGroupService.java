package dev.vorstu.service;

import dev.vorstu.dto.input.CreateGroupRequest;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import dev.vorstu.exception.group.DuplicateGroupNameException;
import dev.vorstu.exception.group.GroupNotFoundException;
import dev.vorstu.exception.group.NotEmptyGroupException;
import dev.vorstu.exception.group.StudentAlreadyPresentsException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.mapper.GroupMapper;
import dev.vorstu.repository.StuddingGroupRepository;
import dev.vorstu.repository.StudentRepository;
import jakarta.transaction.Transactional;
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
    private final GroupMapper mapper;

    @Transactional
    public void addStudent(Long studentId, Long groupId){

        Student student = studentRepository.findById(studentId)
                .orElseThrow(()->new StudentNotFoundException("id="+studentId));
        if(groupId==null){
            student.setGroup(null);
            return;
        }
        StuddingGroup group = studdingGroupRepository.findById(groupId)
                .orElseThrow(()->new GroupNotFoundException("id="+groupId));
        Set<Student> presented = group.getStudents();
        if(presented.contains(student))
            throw new StudentAlreadyPresentsException("student "+student.getFio()+
                    "group "+group.getName());
        student.setGroup(group);
    }

    public GroupInfo getGroup(Long id){
        return mapper.toGroupInfo(studdingGroupRepository.findById(id)
                .orElseThrow(()->new GroupNotFoundException("id="+id)));
    }

    public Page<GroupInfo> getTeacherGroups(Long teacherId, Pageable pageable){
        return studdingGroupRepository.
                findByTeachersId(teacherId, pageable).map(mapper::toGroupInfo);
    }

    public GroupInfo getTeachersGroup(Long teacherId, Long groupId){
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
}

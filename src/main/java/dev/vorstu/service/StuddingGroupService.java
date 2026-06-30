package dev.vorstu.service;

import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.GroupPreview;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import dev.vorstu.exception.group.GroupNotFoundException;
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

}

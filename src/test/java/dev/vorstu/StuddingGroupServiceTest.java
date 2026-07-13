package dev.vorstu;

import dev.vorstu.dto.input.CreateGroupRequest;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.Student;
import dev.vorstu.entity.Teacher;
import dev.vorstu.exception.group.DuplicateGroupNameException;
import dev.vorstu.exception.group.GroupNotFoundException;
import dev.vorstu.exception.group.NotEmptyGroupException;
import dev.vorstu.exception.group.StudentAlreadyPresentsException;
import dev.vorstu.exception.group.TeacherAlreadyTeachesHereException;
import dev.vorstu.exception.student.StudentNotFoundException;
import dev.vorstu.exception.teacher.TeacherNotFoundException;
import dev.vorstu.mapper.GroupMapper;
import dev.vorstu.repository.StuddingGroupRepository;
import dev.vorstu.repository.StudentRepository;
import dev.vorstu.repository.TeacherRepository;
import dev.vorstu.service.StuddingGroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StuddingGroupServiceTest {
    @Mock
    private StuddingGroupRepository studdingGroupRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private GroupMapper mapper;

    @InjectMocks
    private StuddingGroupService studdingGroupService;

    @Test
    void addStudentShouldAddStudentToGroup() {
        Long studentId = 1L;
        Long groupId = 1L;

        Student student = new Student();
        student.setId(studentId);
        student.setFio("Ivanov Ivan");

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");
        group.setStudents(new HashSet<>());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        studdingGroupService.addStudent(studentId, groupId);

        assertThat(student.getGroup()).isEqualTo(group);
        verify(studentRepository).findById(studentId);
        verify(studdingGroupRepository).findById(groupId);
    }

    @Test
    void addStudentShouldThrowExceptionWhenStudentNotFound() {
        Long studentId = 1L;
        Long groupId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.addStudent(studentId, groupId))
                .isInstanceOf(StudentNotFoundException.class);

        verify(studdingGroupRepository, never()).findById(any());
    }

    @Test
    void addStudentShouldThrowExceptionWhenGroupNotFound() {
        Long studentId = 1L;
        Long groupId = 1L;

        Student student = new Student();
        student.setId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.addStudent(studentId, groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addStudentShouldThrowExceptionWhenStudentAlreadyInGroup() {
        Long studentId = 1L;
        Long groupId = 1L;

        Student student = new Student();
        student.setId(studentId);
        student.setFio("Ivanov Ivan");

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");
        group.setStudents(new HashSet<>(Set.of(student)));

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> studdingGroupService.addStudent(studentId, groupId))
                .isInstanceOf(StudentAlreadyPresentsException.class);
    }

    @Test
    void addTeacherShouldAddTeacherToGroup() {
        Long teacherId = 1L;
        Long groupId = 1L;

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFio("Petrov Petr");
        teacher.setGroups(new HashSet<>());

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");
        group.setTeachers(new HashSet<>());

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        studdingGroupService.addTeacher(teacherId, groupId);

        assertThat(group.getTeachers()).contains(teacher);
        assertThat(teacher.getGroups()).contains(group);
    }

    @Test
    void addTeacherShouldThrowExceptionWhenTeacherNotFound() {
        Long teacherId = 1L;
        Long groupId = 1L;

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.addTeacher(teacherId, groupId))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(studdingGroupRepository, never()).findById(any());
    }

    @Test
    void addTeacherShouldThrowExceptionWhenGroupNotFound() {
        Long teacherId = 1L;
        Long groupId = 1L;

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.addTeacher(teacherId, groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addTeacherShouldThrowExceptionWhenTeacherAlreadyTeachesHere() {
        Long teacherId = 1L;
        Long groupId = 1L;

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);
        teacher.setFio("Petrov Petr");

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");
        group.setTeachers(new HashSet<>(Set.of(teacher)));

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> studdingGroupService.addTeacher(teacherId, groupId))
                .isInstanceOf(TeacherAlreadyTeachesHereException.class);
    }

    @Test
    void getGroupShouldReturnGroupInfo() {
        Long groupId = 1L;
        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(groupId)
                .name("GroupA")
                .build();

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.getGroup(groupId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupA");
    }

    @Test
    void getGroupShouldThrowExceptionWhenNotFound() {
        Long groupId = 1L;

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.getGroup(groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void getTeacherGroupsShouldReturnPageOfGroups() {
        Long teacherId = 1L;
        Pageable pageable = Pageable.unpaged();

        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build();

        Page<StuddingGroup> groupPage = new PageImpl<>(List.of(group));
        when(studdingGroupRepository.findByTeachersId(teacherId, pageable)).thenReturn(groupPage);
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        Page<GroupInfo> result = studdingGroupService.getTeacherGroups(teacherId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("GroupA");
    }

    @Test
    void getTeachersGroupShouldReturnGroupInfo() {
        Long teacherId = 1L;
        Long groupId = 1L;

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(groupId)
                .name("GroupA")
                .build();

        when(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherId))
                .thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.getTeachersGroup(teacherId, groupId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupA");
    }

    @Test
    void getTeachersGroupShouldThrowExceptionWhenNotFound() {
        Long teacherId = 1L;
        Long groupId = 1L;

        when(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.getTeachersGroup(teacherId, groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void getTeacherGroupsAuthedShouldReturnPageOfGroups() {
        Long teacherAuthId = 1L;
        Pageable pageable = Pageable.unpaged();

        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build();

        Page<StuddingGroup> groupPage = new PageImpl<>(List.of(group));
        when(studdingGroupRepository.findByTeachersUserAuthId(teacherAuthId, pageable))
                .thenReturn(groupPage);
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        Page<GroupInfo> result = studdingGroupService.getTeacherGroupsAuthed(teacherAuthId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getTeachersGroupAuthedShouldReturnGroupInfo() {
        Long teacherAuthId = 1L;
        Long groupId = 1L;

        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(groupId)
                .name("GroupA")
                .build();

        when(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherAuthId))
                .thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.getTeachersGroupAuthed(teacherAuthId, groupId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupA");
    }

    @Test
    void getTeachersGroupAuthedShouldThrowExceptionWhenNotFound() {
        Long teacherAuthId = 1L;
        Long groupId = 1L;

        when(studdingGroupRepository.findByIdAndTeachersId(groupId, teacherAuthId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.getTeachersGroupAuthed(teacherAuthId, groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void createGroupShouldReturnGroupInfo() {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("GroupA");

        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build();

        when(studdingGroupRepository.existsByName("GroupA")).thenReturn(false);
        when(studdingGroupRepository.save(any(StuddingGroup.class))).thenReturn(group);
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.createGroup(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupA");
        verify(studdingGroupRepository).save(any(StuddingGroup.class));
    }

    @Test
    void createGroupShouldThrowExceptionWhenDuplicateName() {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("GroupA");

        when(studdingGroupRepository.existsByName("GroupA")).thenReturn(true);

        assertThatThrownBy(() -> studdingGroupService.createGroup(request))
                .isInstanceOf(DuplicateGroupNameException.class);

        verify(studdingGroupRepository, never()).save(any());
    }

    @Test
    void editGroupShouldUpdateAndReturnGroupInfo() {
        Long groupId = 1L;
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("GroupB");

        StuddingGroup existingGroup = new StuddingGroup();
        existingGroup.setId(groupId);
        existingGroup.setName("GroupA");

        StuddingGroup updatedGroup = new StuddingGroup();
        updatedGroup.setId(groupId);
        updatedGroup.setName("GroupB");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(groupId)
                .name("GroupB")
                .build();

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup));
        when(studdingGroupRepository.save(existingGroup)).thenReturn(updatedGroup);
        when(mapper.toGroupInfo(updatedGroup)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.editGroup(groupId, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupB");
        verify(studdingGroupRepository).save(existingGroup);
    }

    @Test
    void editGroupShouldThrowExceptionWhenGroupIdNull() {
        assertThatThrownBy(() -> studdingGroupService.editGroup(null, new CreateGroupRequest()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void editGroupShouldThrowExceptionWhenGroupNotFound() {
        Long groupId = 1L;
        CreateGroupRequest request = new CreateGroupRequest();

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.editGroup(groupId, request))
                .isInstanceOf(GroupNotFoundException.class);

        verify(studdingGroupRepository, never()).save(any());
    }

    @Test
    void deleteGroupShouldDeleteGroup() {
        Long groupId = 1L;
        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setStudents(new HashSet<>());

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Long result = studdingGroupService.deleteGroup(groupId);

        assertThat(result).isEqualTo(groupId);
        verify(studdingGroupRepository).deleteById(groupId);
    }

    @Test
    void deleteGroupShouldThrowExceptionWhenGroupIdNull() {
        assertThatThrownBy(() -> studdingGroupService.deleteGroup(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void deleteGroupShouldThrowExceptionWhenGroupNotFound() {
        Long groupId = 1L;

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.deleteGroup(groupId))
                .isInstanceOf(GroupNotFoundException.class);

        verify(studdingGroupRepository, never()).deleteById(any());
    }

    @Test
    void deleteGroupShouldThrowExceptionWhenGroupHasStudents() {
        Long groupId = 1L;
        StuddingGroup group = new StuddingGroup();
        group.setId(groupId);
        group.setStudents(new HashSet<>(Set.of(new Student())));

        when(studdingGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> studdingGroupService.deleteGroup(groupId))
                .isInstanceOf(NotEmptyGroupException.class);

        verify(studdingGroupRepository, never()).deleteById(any());
    }

    @Test
    void getStudentGroupAuthedShouldReturnGroupInfo() {
        Long authId = 1L;
        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build();

        when(studdingGroupRepository.findByStudentsUserAuthId(authId))
                .thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.getStudentGroupAuthed(authId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GroupA");
    }

    @Test
    void getStudentGroupAuthedShouldReturnNullWhenNotFound() {
        Long authId = 1L;

        when(studdingGroupRepository.findByStudentsUserAuthId(authId))
                .thenReturn(Optional.empty());

        GroupInfo result = studdingGroupService.getStudentGroupAuthed(authId);

        assertThat(result).isNull();
    }

    @Test
    void getAllGroupsShouldReturnPageOfGroups() {
        Pageable pageable = Pageable.unpaged();

        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName("GroupA");

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name("GroupA")
                .build();

        Page<StuddingGroup> groupPage = new PageImpl<>(List.of(group));
        when(studdingGroupRepository.findAll(pageable)).thenReturn(groupPage);
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        Page<GroupInfo> result = studdingGroupService.getAllGroups(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("GroupA");
    }

    @Test
    void getGroupByNameShouldReturnGroupInfo() {
        String groupName = "GroupA";
        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName(groupName);

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name(groupName)
                .build();

        when(studdingGroupRepository.findByName(groupName)).thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.getGroupByName(groupName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(groupName);
    }

    @Test
    void getGroupByNameShouldThrowExceptionWhenNotFound() {
        String groupName = "GroupA";

        when(studdingGroupRepository.findByName(groupName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studdingGroupService.getGroupByName(groupName))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void findOrCreateByNameShouldReturnExistingGroup() {
        String groupName = "GroupA";
        StuddingGroup group = new StuddingGroup();
        group.setId(1L);
        group.setName(groupName);

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name(groupName)
                .build();

        when(studdingGroupRepository.findByName(groupName)).thenReturn(Optional.of(group));
        when(mapper.toGroupInfo(group)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.findOrCreateByName(groupName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(groupName);
        verify(studdingGroupRepository, never()).save(any());
    }

    @Test
    void findOrCreateByNameShouldCreateNewGroupWhenNotFound() {
        String groupName = "GroupA";
        StuddingGroup newGroup = new StuddingGroup();
        newGroup.setId(1L);
        newGroup.setName(groupName);

        GroupInfo groupInfo = GroupInfo.builder()
                .id(1L)
                .name(groupName)
                .build();

        when(studdingGroupRepository.findByName(groupName)).thenReturn(Optional.empty());
        when(studdingGroupRepository.save(any(StuddingGroup.class))).thenReturn(newGroup);
        when(mapper.toGroupInfo(newGroup)).thenReturn(groupInfo);

        GroupInfo result = studdingGroupService.findOrCreateByName(groupName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(groupName);
        verify(studdingGroupRepository).save(any(StuddingGroup.class));
    }
}
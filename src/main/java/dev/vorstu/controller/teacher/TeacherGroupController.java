package dev.vorstu.controller.teacher;

import dev.vorstu.controller.annotations.CurrentUser;
import dev.vorstu.dto.output.*;
import dev.vorstu.entity.Teacher;
import dev.vorstu.exception.teacher.DoesntTeachThisGroupException;
import dev.vorstu.service.StuddingGroupService;
import dev.vorstu.service.StudentService;
import dev.vorstu.service.TeacherService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.GroupSequence;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RestController
@PreAuthorize("hasRole('TEACHER')")
@RequestMapping("/api/teacher/groups")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TeacherGroupController {
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final StuddingGroupService studdingGroupService;


    @GetMapping("/my")
    public Page<GroupInfo> myGroups(
            @CurrentUser Long id,
            @ParameterObject
            Pageable pageable){
        return studdingGroupService.getTeacherGroupsAuthed(id, pageable);
    }

    /// TODONE
    @GetMapping("/my/{id}")
    public GroupInfo myGroup(@CurrentUser Long id, @PathVariable("id") Long groupId){
        return studdingGroupService.getTeachersGroupAuthed(id, groupId);
    }


}

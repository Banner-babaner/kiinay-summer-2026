package dev.vorstu.controller.student;

import dev.vorstu.controller.annotations.CurrentUser;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.dto.output.StudentInfo;
import dev.vorstu.service.StuddingGroupService;
import dev.vorstu.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/groups")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@SecurityRequirement(name = "Bearer Authentication")
public class StudentGroupController {
    private final StuddingGroupService groupService;
    private final StudentService studentService;

    @PutMapping("/my")
    public GroupInfo addStudentToGroup(@CurrentUser Long authId){
        StudentInfo student = studentService.getByAuthId(authId);
        if(student.getGroup()==null) return null;
        return groupService.getGroup(student.getGroup().getId());
    }

}

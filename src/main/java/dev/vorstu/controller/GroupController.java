package dev.vorstu.controller;

import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.service.StuddingGroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class GroupController {
    private final StuddingGroupService service;

    @PutMapping("{id}")
    public void addStudentToGroup(@PathVariable("id") Long groupId,
                                  Long studentId){
        service.addStudent(studentId, groupId);
    }

    @GetMapping("{id}")
    public GroupInfo getGroup(@PathVariable("id") Long groupId){
        return service.getGroup(groupId);
    }
}

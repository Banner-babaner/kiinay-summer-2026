package dev.vorstu.controller.admin;

import dev.vorstu.dto.input.CreateGroupRequest;
import dev.vorstu.dto.output.GroupInfo;
import dev.vorstu.service.StuddingGroupService;
import dev.vorstu.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminGroupController {
    private final StuddingGroupService groupService;

    @GetMapping
    public Page<GroupInfo> getAllGroups(
            @ParameterObject
            Pageable pageable){
        return groupService.getAllGroups(pageable);
    }

    @GetMapping("/{id}")
    public GroupInfo getGroup(@PathVariable("id") Long groupId){
        return groupService.getGroup(groupId);
    }

    @PostMapping
    public ResponseEntity<GroupInfo> createGroup(
            @Valid @RequestBody
            CreateGroupRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupInfo> editGroup(@PathVariable("id") Long groupId,
                                               @Valid @RequestBody
                                               CreateGroupRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.editGroup(groupId, request));
    }



    @DeleteMapping("/{id}")
    public Long deleteGroup(@PathVariable("id") Long groupId){
        return groupService.deleteGroup(groupId);
    }
}

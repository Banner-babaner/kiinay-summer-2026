package dev.vorstu.dto.output;

import dev.vorstu.entity.StuddingGroup;
import dev.vorstu.entity.UserAuth;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherInfo {
    private Long id;
    private String fio;
    private String phoneNumber;
    private Set<GroupPreview> groups;
    private String email;
}

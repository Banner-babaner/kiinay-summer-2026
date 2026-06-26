package dev.vorstu.application.student.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
    private Long id;
    private String fio;
    private String group;
    private String phoneNumber;
}

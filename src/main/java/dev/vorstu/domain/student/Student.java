package dev.vorstu.domain.student;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="students")
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    public Student(String fio, String group, String phoneNumber) {
        this.fio = fio;
        this.group = group;
        this.phoneNumber = phoneNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    @NotNull
    @NotBlank
    @Size(max = 64)
    @Column(nullable = false)
    private String fio;
    @Getter
    @Setter
    @Size(max = 64)
    @Column(name = "group_of_students")
    private String group;
    @Getter
    @Setter
    @Size(max = 24)
    private String phoneNumber;
}

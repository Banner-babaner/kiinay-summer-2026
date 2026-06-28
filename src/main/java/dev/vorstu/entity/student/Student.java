package dev.vorstu.entity.student;

import dev.vorstu.entity.auth.UserAuth;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Entity
@Table(name="students")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    public Student(String fio, String group, String phoneNumber) {
        this.fio = fio;
        this.group = group;
        this.phoneNumber = phoneNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull
    @NotBlank
    @Size(max = 64)
    @Column(nullable = false)
    String fio;
    @Size(max = 64)
    @Column(name = "group_of_students")
    String group;
    @Size(max = 24)
    String phoneNumber;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    UserAuth userAuth;
}

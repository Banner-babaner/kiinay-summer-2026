package dev.vorstu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import javax.swing.*;

@Getter
@Setter
@Entity
@Table(name="students")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    public Student(String fio, String phoneNumber) {
        this.fio = fio;
        this.phoneNumber = phoneNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    @Size(max = 64)
    @Column(nullable = false)
    private String fio;
    @Size(max = 24)
    private String phoneNumber;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private UserAuth userAuth;
    @ManyToOne
    private StuddingGroup group;
}

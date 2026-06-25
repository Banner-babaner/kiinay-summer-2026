package dev.vorstu.dto;

import jakarta.persistence.*;
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
    private String fio;
    @Getter
    @Setter
    @Column(name = "group_of_students")
    private String group;
    @Getter
    @Setter
    private String phoneNumber;
}

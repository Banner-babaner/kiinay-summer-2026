package dev.vorstu.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="groups")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StuddingGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 64)
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Student> students;
    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private Set<Teacher> teachers;
}

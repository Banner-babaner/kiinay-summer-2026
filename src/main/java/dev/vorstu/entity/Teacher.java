package dev.vorstu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import javax.swing.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="teachers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher implements Authable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    @Size(max = 64)
    @Column(nullable = false)
    private String fio;
    @Embedded
    private ContactData contacts;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
    private UserAuth userAuth;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_groups",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<StuddingGroup> groups;

    @Override
    public void setAuth(UserAuth auth) {
        userAuth=auth;
    }
}

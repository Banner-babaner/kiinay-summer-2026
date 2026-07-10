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
public class Student implements Authable{

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
    @ManyToOne(cascade = CascadeType.MERGE)
    private StuddingGroup group;

    @Override
    @OneToOne(cascade = CascadeType.PERSIST)
    public void setAuth(UserAuth auth) {
        userAuth=auth;
    }
}

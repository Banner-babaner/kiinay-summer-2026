package dev.vorstu.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user_auths")
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Size(max = 70)
    @Column(nullable = false)
    String login;
    @Size(max = 70)
    @Column(nullable = false)
    String password;
    @Enumerated(EnumType.STRING)
    UserRole role;
}

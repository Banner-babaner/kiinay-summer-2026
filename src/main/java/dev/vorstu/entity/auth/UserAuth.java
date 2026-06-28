package dev.vorstu.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "user_auths")
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role).stream().map(
                r->new SimpleGrantedAuthority("ROLE_"+r)
        ).toList();
    }

    @Override
    public String getUsername() {
        return login;
    }
}

package dev.vorstu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Struct;
import org.hibernate.boot.jaxb.mapping.GenerationTiming;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Table(name = "invite_applications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InviteApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedDate
    @PastOrPresent
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expiresAt;


    @Generated(event = EventType.INSERT)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID secretKey;

    @Setter
    @NotNull
    @Builder.Default
    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean used = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Email
    @NotNull
    @NotBlank
    @Size(max = 256)
    @Column(nullable = false)
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}$")
    private String phoneNumber;

    @Size(max = 64)
    private String groupName;

    @NotNull
    @Size(max = 64)
    @Column(nullable = false)
    private String fio;

    @Size(max = 70)
    @Column(nullable = false)
    String login;

    @Size(max = 70)
    @Column(nullable = false)
    String password;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}

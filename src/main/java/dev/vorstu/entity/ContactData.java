package dev.vorstu.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactData {
    @Pattern(regexp = "\\+?[0-9]{10,15}$")
    private String phoneNumber;
    @Email
    @Size(max = 255)
    private String email;
}

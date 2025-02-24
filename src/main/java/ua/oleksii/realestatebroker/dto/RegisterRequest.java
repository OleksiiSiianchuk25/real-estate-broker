package ua.oleksii.realestatebroker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ua.oleksii.realestatebroker.model.User.Role;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;

    @NotBlank
    private String phone;

    private String telegram;
    private Role role;
    private String agency;
}

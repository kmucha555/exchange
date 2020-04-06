package pl.mkjb.exchange.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.mkjb.exchange.infrastructure.mvc.validator.EqualFields;
import pl.mkjb.exchange.infrastructure.mvc.validator.Password;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@ToString
@EqualFields(field = "password", fieldConfirm = "confirmPassword", fieldMatchName = "password")
public class UserDto {
    private long id;

    @NotBlank
    @Size(min = 3, max = 32)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 32)
    private String lastName;

    @NotBlank
    @Size(min = 5, max = 32)
    private String userName;

    @Password
    private String password;

    @Password
    private String confirmPassword;
}
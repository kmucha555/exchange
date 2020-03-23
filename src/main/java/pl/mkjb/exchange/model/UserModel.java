package pl.mkjb.exchange.model;

import lombok.Builder;
import lombok.Data;
import pl.mkjb.exchange.validator.EqualFields;
import pl.mkjb.exchange.validator.Password;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Data
@EqualFields(field = "password", fieldConfirm = "confirmPassword", fieldMatchName = "password")
public class UserModel {
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
package pl.mkjb.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mkjb.exchange.validator.EqualFields;
import pl.mkjb.exchange.validator.Password;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
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
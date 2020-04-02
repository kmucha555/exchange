package pl.mkjb.exchange.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.mkjb.exchange.validator.EqualFields;
import pl.mkjb.exchange.validator.Password;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
@EqualFields(field = "password", fieldConfirm = "confirmPassword", fieldMatchName = "password")
public class UserModel {
    private long id;

    //    @NotBlank
//    @Size(min = 3, max = 32)
    private String firstName;

    //    @NotBlank
//    @Size(min = 3, max = 32)
    private String lastName;

    //    @NotBlank
//    @Size(min = 5, max = 32)
    private String userName;

    //    @Password
    private String password;

    //    @Password
    private String confirmPassword;

    @JsonCreator
    public UserModel(@JsonProperty("id") long id,
                     @JsonProperty("firstName") @NotBlank @Size(min = 3, max = 32) String firstName,
                     @JsonProperty("lastName") @NotBlank @Size(min = 3, max = 32) String lastName,
                     @JsonProperty("userName") @NotBlank @Size(min = 5, max = 32) String userName,
                     @JsonProperty("password") @Password String password,
                     @JsonProperty("confirmPassword") @Password String confirmPassword) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
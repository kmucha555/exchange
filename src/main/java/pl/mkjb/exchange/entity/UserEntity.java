package pl.mkjb.exchange.entity;

import lombok.*;
import pl.mkjb.exchange.model.UserModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users", indexes = @Index(columnList = "user_name", unique = true))
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", length = 32)
    private String firstName;

    @Column(name = "last_name", length = 32)
    private String lastName;

    @Column(name = "user_name", length = 32)
    private String username;

    private Boolean active;

    @Column(length = 60)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.active = Boolean.TRUE;
        this.createdAt = LocalDateTime.now();
    }

    public static UserEntity fromModel(UserModel userModel) {
        return UserEntity.builder()
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .username(userModel.getUserName())
                .password(userModel.getPassword())
                .build();
    }
}

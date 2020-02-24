package pl.mkjb.exchange.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = @Index(columnList = "user_name", unique = true))
@NoArgsConstructor
@Data
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

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.active = Boolean.TRUE;
        this.createdAt = LocalDateTime.now();
    }
}

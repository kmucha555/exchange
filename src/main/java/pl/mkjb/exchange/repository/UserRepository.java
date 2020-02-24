package pl.mkjb.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mkjb.exchange.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String userName);
}

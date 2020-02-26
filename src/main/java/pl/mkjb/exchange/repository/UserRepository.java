package pl.mkjb.exchange.repository;

import org.springframework.data.repository.CrudRepository;
import pl.mkjb.exchange.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String userName);
}

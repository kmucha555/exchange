package pl.mkjb.exchange.repository;

import org.springframework.data.repository.CrudRepository;
import pl.mkjb.exchange.entity.RoleEntity;
import pl.mkjb.exchange.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Set<UserEntity> findByRolesContaining(RoleEntity roleEntity);
}

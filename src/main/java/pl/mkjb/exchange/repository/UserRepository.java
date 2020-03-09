package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;
import pl.mkjb.exchange.entity.RoleEntity;
import pl.mkjb.exchange.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Option<UserEntity> findByUsername(String username);

    Set<UserEntity> findByRolesContaining(RoleEntity roleEntity);
}

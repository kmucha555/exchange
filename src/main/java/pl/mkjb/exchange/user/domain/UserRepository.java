package pl.mkjb.exchange.user.domain;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Option<UserEntity> findByUsername(String username);

    Set<UserEntity> findByRolesContaining(RoleEntity roleEntity);
}

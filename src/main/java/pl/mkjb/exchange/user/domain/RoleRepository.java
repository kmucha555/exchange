package pl.mkjb.exchange.user.domain;

import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Integer> {
    Option<RoleEntity> findByRole(String role);
}
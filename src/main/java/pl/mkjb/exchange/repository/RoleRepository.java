package pl.mkjb.exchange.repository;

import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.RoleEntity;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Integer> {
    Option<RoleEntity> findByRole(String role);
}
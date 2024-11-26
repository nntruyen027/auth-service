package qbit.entier.microservice.repository;

import org.springframework.stereotype.Repository;
import qbit.entier.microservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

}
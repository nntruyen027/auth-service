package qbit.entier.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(User user, Role role);

    @Procedure(name = "deleteUserRoleByUserName")
    void deleteAllByUserName(String username);

    List<UserRole> findByUserId(Long id);

    @Procedure(name = "findRolesByUsername")
    void assignRoleToUser(String username, String role);
}

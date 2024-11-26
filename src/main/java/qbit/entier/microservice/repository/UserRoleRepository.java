package qbit.entier.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserAndRole(User user, Role role);
    // Bạn có thể thêm các truy vấn tùy chỉnh nếu cần
    @Procedure(name = "findRolesByUsername")
    List<Object[]> findRolesByUsername(String username);

    @Procedure(name = "findRolesByUsername")
    void assignRoleToUser(String username, String role);
}

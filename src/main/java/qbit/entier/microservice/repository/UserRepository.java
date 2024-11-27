package qbit.entier.microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import qbit.entier.microservice.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<Object> findByEmail(String email);

    @Procedure(name = "updateUser")
    Optional<User> updateUser(Long id, String username, String email, String googleId, String facebookId);

}
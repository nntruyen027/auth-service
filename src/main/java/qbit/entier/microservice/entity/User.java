package qbit.entier.microservice.entity;

import jakarta.persistence.*;
import lombok.*;
import qbit.entier.microservice.dto.RoleDto;
import qbit.entier.microservice.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String facebookId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Phương thức chuyển đổi từ User sang UserDto
    public UserDto toDto() {
        return new UserDto(
                this.id,
                this.username,
                this.password,
                this.email,
                this.googleId,
                this.facebookId,
                this.createdAt,
                this.updatedAt,
                this.roles
        );
    }
}

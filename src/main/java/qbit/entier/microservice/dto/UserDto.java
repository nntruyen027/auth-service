package qbit.entier.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import qbit.entier.microservice.entity.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String facebookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleDto> roles;  // Chứa danh sách RoleDto

    public UserDto(Long id, String username,
                   String password, String email,
                   String googleId, String facebookId,
                   LocalDateTime createdAt,
                   LocalDateTime updatedAt, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.googleId = googleId;
        this.facebookId = facebookId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.roles = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))  // Mapping từ Role sang RoleDto
                .collect(Collectors.toSet());
    }
}

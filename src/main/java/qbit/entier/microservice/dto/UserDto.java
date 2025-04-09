package qbit.entier.microservice.dto;

import lombok.*;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String facebookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleDto> roles;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Boolean isMale;
    private String avatar;

    public static UserDto fromEntity(User entity) {
        return UserDto.builder().
                id(entity.getId())
                .phoneNumber(entity.getPhoneNumber())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .address(entity.getAddress())
                .avatar(entity.getAvatar())
                .roles(entity.getRoles().stream()
                        .map(role -> new RoleDto(role.getId(), role.getRoleName(), role.getDescription()))  // Mapping từ Role sang RoleDto
                        .collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .facebookId(entity.getFacebookId())
                .googleId(entity.getGoogleId())
                .email(entity.getEmail())
                .isMale(entity.getIsMale())
                .fullName(entity.getFullName())
                .build();
    }
}

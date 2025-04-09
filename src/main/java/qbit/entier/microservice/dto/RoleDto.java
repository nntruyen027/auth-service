package qbit.entier.microservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleDto {
    private Long id;
    private String roleName;
    private String description;
}


package qbit.entier.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Instant tokenExpiry;
    private String username;
    private String email;
    private List<String> roles;
    private Long userId;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Boolean isMale;
    private String avatar;
}

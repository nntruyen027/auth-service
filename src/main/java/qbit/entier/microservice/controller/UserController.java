package qbit.entier.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import qbit.entier.microservice.dto.LoginResponse;
import qbit.entier.microservice.dto.UserDto;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.service.CustomUserDetailsService;

import java.util.List;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) throws Exception {
        User newUser = customUserDetailsService.registerUser(
                user.getUsername(), user.getPassword(), user.getEmail());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        LoginResponse response = customUserDetailsService.authenticateUser(username, password);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('super_admin')")
    @PostMapping("/{username}/roles")
    public ResponseEntity<?> assignRolesToUser(@PathVariable String username, @RequestBody List<String> roles) throws Exception {
        customUserDetailsService.assignRolesToUser(username, roles);
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable Long id) {
        return customUserDetailsService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/self")
    public ResponseEntity<UserDto> getSelfUser() throws Exception {
        return ResponseEntity.ok(customUserDetailsService.getSelfUser());
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) throws Exception {
        return ResponseEntity.ok(customUserDetailsService.updateUser(id, user));
    }

    @PutMapping("/self/password")
    public ResponseEntity<?> updateSelfPassword(@RequestBody String newPassword) throws Exception {
        customUserDetailsService.updateSelfPassword(newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws Exception {
        customUserDetailsService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

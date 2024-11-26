package qbit.entier.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import qbit.entier.microservice.dto.LoginResponse;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.service.CustomUserDetailsService;
import qbit.entier.microservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = customUserDetailsService.registerUser(
                    user.getUsername(), user.getPassword(), user.getEmail());
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            LoginResponse response = customUserDetailsService.authenticateUser(username, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('super_admin')")
    @PostMapping("/{username}/roles")
    public ResponseEntity<?> assignRolesToUser(@PathVariable String username, @RequestBody List<String> roles) {
        try {
            customUserDetailsService.assignRolesToUser(username, roles);
            return ResponseEntity.ok("Roles assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return customUserDetailsService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

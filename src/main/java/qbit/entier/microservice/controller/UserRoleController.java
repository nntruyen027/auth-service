package qbit.entier.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import qbit.entier.microservice.service.UserRoleService;

import java.util.List;

@RestController
@RequestMapping("/user-roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    // Gán vai trò cho người dùng
    @PostMapping("/{userId}")
    public ResponseEntity<Void> assignRolesToUser(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        try {
            userRoleService.assignRolesToUser(userId, roleNames);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Lấy vai trò của người dùng theo ID
    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getRolesForUser(@PathVariable Long userId) {
        List<String> roles = userRoleService.getRolesForUser(userId);
        return ResponseEntity.ok(roles);
    }

    // Xóa vai trò của người dùng
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeRolesFromUser(@PathVariable Long userId, @RequestBody List<String> roleNames) {
        try {
            userRoleService.removeRolesFromUser(userId, roleNames);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}

package qbit.entier.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Lấy tất cả vai trò
    @GetMapping
    public ResponseEntity<Page<Role>> getAllRoles(Pageable pageable) {
        Page<Role> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    // Lấy vai trò theo ID
    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    // Tạo vai trò mới
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.ok(createdRole);
    }
}

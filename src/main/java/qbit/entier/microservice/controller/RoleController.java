package qbit.entier.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import qbit.entier.microservice.dto.RoleDto;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('admin')")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<Page<RoleDto>> getAllRoles(Pageable pageable) {
        Page<RoleDto> roles = roleService.getAllRoles(pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long roleId) {
        RoleDto role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody Role role) {
        RoleDto createdRole = roleService.createRole(role);
        return ResponseEntity.ok(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}

package qbit.entier.microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Lấy tất cả vai trò
    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    // Lấy vai trò theo ID
    public Role getRoleById(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        return role.orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // Tạo vai trò mới
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}

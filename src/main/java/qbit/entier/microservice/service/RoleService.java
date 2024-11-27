package qbit.entier.microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import qbit.entier.microservice.dto.RoleDto;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Lấy tất cả vai trò
    public Page<RoleDto> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(role -> new RoleDto(role.getId(), role.getRoleName()));
    }

    // Lấy vai trò theo ID
        public RoleDto getRoleById(Long roleId) {
            return roleRepository.findById(roleId).map(role -> new RoleDto(role.getId(), role.getRoleName())).get();
        }

    // Tạo vai trò mới
    public RoleDto createRole(Role role) {
        Role newRole = roleRepository.save(role);
        return new RoleDto(newRole.getId(), newRole.getRoleName());
    }

    public RoleDto updateRole(Long id, Role role) {
        Role existingRole = roleRepository.findById(id).get();

        existingRole.setRoleName(role.getRoleName());

        Role updatedRole = roleRepository.save(existingRole);
        return new RoleDto(updatedRole.getId(), updatedRole.getRoleName());
    }

    public void deleteRole(Long id) {
        Role existingRole = roleRepository.findById(id).get();
        roleRepository.delete(existingRole);
    }
}

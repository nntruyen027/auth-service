package qbit.entier.microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.repository.RoleRepository;
import qbit.entier.microservice.repository.UserRepository;
import qbit.entier.microservice.repository.UserRoleRepository;

import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    // Gán vai trò cho người dùng
    public void assignRolesToUser(Long userId, List<String> roleNames) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        for (String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new Exception("Role " + roleName + " not found"));

            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        }

        userRepository.save(user);
    }

    // Lấy vai trò của người dùng
    public List<String> getRolesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().stream().map(Role::getRoleName).toList();
    }

    // Xóa vai trò của người dùng
    public void removeRolesFromUser(Long userId, List<String> roleNames) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        for (String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new Exception("Role " + roleName + " not found"));

            user.getRoles().remove(role);
        }

        userRepository.save(user);
    }
}

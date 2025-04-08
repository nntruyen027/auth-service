package qbit.entier.microservice.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.repository.UserRepository;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String UPLOADS_DIR = "uploads";

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing data...");

        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEmail("admin@gmail.com");
            adminUser.setFullName("Quản trị viên tối cao");

            if (adminUser.getRoles() == null) {
                adminUser.setRoles(new HashSet<>());  // Khởi tạo Set roles
            }

            Role adminRole = roleRepository.findByRoleName("super_admin").orElse(null);
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRoleName("super_admin");
                roleRepository.save(adminRole);
            }

            Role adminRole1 = roleRepository.findByRoleName("admin").orElse(null);
            if (adminRole1 == null) {
                adminRole1 = new Role();
                adminRole1.setRoleName("admin");
                roleRepository.save(adminRole1);
            }

            adminUser.getRoles().add(adminRole);
            adminUser.getRoles().add(adminRole1);

            userRepository.save(adminUser);

            System.out.println("Superuser created: admin / admin");
        }
    }
}

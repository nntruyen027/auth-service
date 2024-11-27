package qbit.entier.microservice.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;

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
        System.out.println("Initializing data...");  // Đảm bảo DataInitializer được chạy
        createUploadsDirectory();

        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEmail("admin@gmail.com");

            if (adminUser.getRoles() == null) {
                adminUser.setRoles(new HashSet<>());  // Khởi tạo Set roles
            }

            Role adminRole = roleRepository.findByRoleName("super_admin").orElse(null);
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRoleName("super_admin");
                roleRepository.save(adminRole);
            }

            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);

            System.out.println("Superuser created: admin / admin");
        }
    }

    private void createUploadsDirectory() {
        Path path = Paths.get(UPLOADS_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println("Uploads directory created.");
            } catch (Exception e) {
                System.out.println("Failed to create uploads directory.");
                e.printStackTrace();
            }
        }
    }
}

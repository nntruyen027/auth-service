package qbit.entier.microservice.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        createUploadsDirectory();

        // Kiểm tra xem user admin đã tồn tại chưa
        if (userRepository.findByUsername("admin") == null) {
            // Tạo user admin mới
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEmail("admin@example.com");

            // Tạo role ADMIN nếu chưa có
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElse(null);
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setRoleName("ADMIN");
                roleRepository.save(adminRole);
            }

            // Gán role ADMIN cho user admin
            adminUser.getRoles().add(adminRole);

            // Lưu user admin vào cơ sở dữ liệu
            userRepository.save(adminUser);

            System.out.println("Superuser created: admin / admin");
        }
    }

    // Tạo thư mục uploads nếu chưa tồn tại
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

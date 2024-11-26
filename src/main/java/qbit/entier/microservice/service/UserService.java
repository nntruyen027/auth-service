package qbit.entier.microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import qbit.entier.microservice.dto.LoginResponse;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Đăng ký người dùng
    public User registerUser(User user) {
        return userRepository.save(user);
    }


    // Lấy tất cả người dùng
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // Lấy người dùng theo ID
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }
}

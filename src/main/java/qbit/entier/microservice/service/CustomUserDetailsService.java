package qbit.entier.microservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qbit.entier.microservice.config.JwtFilter;
import qbit.entier.microservice.dto.LoginResponse;
import qbit.entier.microservice.dto.RoleDto;
import qbit.entier.microservice.entity.Role;
import qbit.entier.microservice.entity.User;
import qbit.entier.microservice.entity.UserRole;
import qbit.entier.microservice.repository.RoleRepository;
import qbit.entier.microservice.repository.UserRepository;
import qbit.entier.microservice.repository.UserRoleRepository;
import qbit.entier.microservice.util.JwtUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<RoleDto> roles = userRoleRepository.findRolesByUsername(user.getUsername())
				.stream().map(RoleDto::new).toList();

		List<SimpleGrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
				.collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				authorities
		);
	}

	public User createUser(User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("User already exists: " + user.getUsername());
		}

		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public LoginResponse authenticateUser(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));

		String token = generateJwtToken(username);

		return new LoginResponse(token);
	}

	private String generateJwtToken(String username) {
		// Replace with actual JWT generation logic
		return jwtUtil.generateToken(username);
	}

	@Transactional
	public User registerUser(String username, String password, String email) throws Exception {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new Exception("Username already exists");
		}

		if (userRepository.findByEmail(email).isPresent()) {
			throw new Exception("Email already exists");
		}

		User user = new User();
		Date now = new Date();
		LocalDateTime localDateTime = now.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		user.setUsername(username);
		user.setPassword(encoder.encode(password));
		user.setEmail(email);
		user.setCreatedAt(localDateTime);
		user.setUpdatedAt(localDateTime);

		return userRepository.save(user);
	}

	@Transactional
	public void updatePassword(Long userId, String newPassword) throws Exception {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new Exception("User not found"));

		user.setPassword(encoder.encode(newPassword));
		Date now = new Date();
		LocalDateTime localDateTime = now.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		user.setUpdatedAt(localDateTime);
		userRepository.save(user);
	}

	@Transactional
	public void assignRolesToUser(String username, List<String> roleNames) throws Exception {


		roleNames.forEach(s -> {
			userRoleRepository.assignRoleToUser(username, s);
		});
	}

	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean checkUserExistence(String username, String email) {
		return userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent();
	}
}

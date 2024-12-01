package qbit.entier.microservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qbit.entier.microservice.config.JwtFilter;
import qbit.entier.microservice.dto.LoginResponse;
import qbit.entier.microservice.dto.RoleDto;
import qbit.entier.microservice.dto.UserDto;
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

	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
		return userDetails.getUsername();
	}

	public UserDto getSelfUser() throws Exception {
		String username = getCurrentUsername();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new Exception("User not found"));

		return user.toDto();
	}


	public User createUser(User user) {
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

	public void updateSelfPassword(String newPassword) throws Exception {
		String username = getCurrentUsername();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new Exception("User not found"));

		user.setPassword(encoder.encode(newPassword));
		Date now = new Date();
		LocalDateTime localDateTime = now.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		user.setUpdatedAt(localDateTime);
		userRepository.save(user);
	}

	public UserDto updateSelfUser(Long id, User user) throws Exception {
		String username = getCurrentUsername();

		User existingUser = userRepository.findByUsername(username)
				.orElseThrow(() -> new Exception("User not found"));

		if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
			existingUser.setUsername(user.getUsername().trim());
		}
		if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
			existingUser.setEmail(user.getEmail().trim());
		}
		if (user.getGoogleId() != null && !user.getGoogleId().trim().isEmpty()) {
			existingUser.setGoogleId(user.getGoogleId().trim());
		}
		if (user.getFacebookId() != null && !user.getFacebookId().trim().isEmpty()) {
			existingUser.setFacebookId(user.getFacebookId().trim());
		}

		existingUser.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(existingUser);

		return updatedUser.toDto();
	}


	public UserDto updateUser(Long id, User user) throws Exception {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new Exception("User not found"));

		if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
			existingUser.setUsername(user.getUsername().trim());
		}
		if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
			existingUser.setEmail(user.getEmail().trim());
		}
		if (user.getGoogleId() != null && !user.getGoogleId().trim().isEmpty()) {
			existingUser.setGoogleId(user.getGoogleId().trim());
		}
		if (user.getFacebookId() != null && !user.getFacebookId().trim().isEmpty()) {
			existingUser.setFacebookId(user.getFacebookId().trim());
		}

		existingUser.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(existingUser);

		return updatedUser.toDto();
	}

	@Transactional
	public void assignRolesToUser(String username, List<String> roleNames) throws Exception {
		roleNames.forEach(s -> {
			userRoleRepository.assignRoleToUser(username, s);
		});
	}

	public Optional<UserDto> getUserById(Long id) {
		return Optional.ofNullable(userRepository.findUserById(id).get().toDto());
	}

	public void deleteUserById(Long id) throws Exception {
		User user = userRepository.findUserById(id)
				.orElseThrow(() -> new Exception("User not found"));
		userRepository.delete(user);
	}

}

package qbit.entier.microservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.time.Instant;
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

		List<RoleDto> roles = userRoleRepository.findByUserId(user.getId()).stream()
				.map((e) -> {
					return RoleDto.builder().roleName(e.getRole().getRoleName()).build();
				}).toList();

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

		return UserDto.fromEntity(user);
	}


	public User createUser(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public LoginResponse authenticateUser(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = generateJwtToken(username);
		Instant tokenExpiry = jwtUtil.getTokenExpiry(token);

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
				.map((e) -> e.getRole().getRoleName()).toList();

		return new LoginResponse(
				token,
				tokenExpiry,
				user.getUsername(),
				user.getEmail(),
				roles,
				user.getId(),
				user.getFullName(),
				user.getAddress(),
				user.getPhoneNumber(),
				user.getIsMale(),
				user.getAvatar()
		);
	}



	private String generateJwtToken(String username) {
		// Replace with actual JWT generation logic
		return jwtUtil.generateToken(username);
	}

	@Transactional
	public User registerUser(User regiterUser) throws Exception {
		if (userRepository.findByUsername(regiterUser.getUsername()).isPresent()) {
			throw new Exception("Username already exists");
		}

		if (userRepository.findByEmail(regiterUser.getEmail()).isPresent()) {
			throw new Exception("Email already exists");
		}

		Date now = new Date();
		LocalDateTime localDateTime = now.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		regiterUser.setPassword(encoder.encode(regiterUser.getPassword()));
		regiterUser.setCreatedAt(localDateTime);
		regiterUser.setUpdatedAt(localDateTime);

		return userRepository.save(regiterUser);
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

	public void updateSelfPassword(String oldPassword, String newPassword) throws Exception {
		String username = getCurrentUsername();

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new Exception("User not found"));

		if (!encoder.matches(oldPassword, user.getPassword())) {
			throw new Exception("Old password is incorrect");
		}

		user.setPassword(encoder.encode(newPassword));
		user.setUpdatedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	public UserDto updateSelfUser(User user) throws Exception {
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
		if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
			existingUser.setFullName(user.getFullName().trim());
		}
		if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
			existingUser.setPhoneNumber(user.getPhoneNumber().trim());
		}
		if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
			existingUser.setAvatar(user.getAvatar().trim());
		}
		if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
			existingUser.setAddress(user.getAddress().trim());
		}
		if (user.getIsMale() != null) {
			existingUser.setIsMale(user.getIsMale());
		}


		existingUser.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(existingUser);

		return UserDto.fromEntity(updatedUser);
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
		if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
			existingUser.setFullName(user.getFullName().trim());
		}
		if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
			existingUser.setPhoneNumber(user.getPhoneNumber().trim());
		}
		if (user.getAvatar() != null) {
			existingUser.setAvatar(user.getAvatar().trim());
		}
		else {
			existingUser.setAvatar("");
		}
		if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
			existingUser.setAddress(user.getAddress().trim());
		}
		if (user.getIsMale() != null) {
			existingUser.setIsMale(user.getIsMale());
		}

		existingUser.setUpdatedAt(LocalDateTime.now());

		User updatedUser = userRepository.save(existingUser);

		return UserDto.fromEntity(updatedUser) ;
	}

	@Transactional
	public void assignRolesToUser(String username, List<String> roleNames) throws Exception {
		userRoleRepository.deleteAllByUserName(username);
		roleNames.forEach(s -> {
			userRoleRepository.assignRoleToUser(username, s);
		});
	}

	public Optional<UserDto> getUserById(Long id) {
		return Optional.ofNullable(UserDto.fromEntity(userRepository.findUserById(id).get()));
	}

	public List<UserDto> getUsersByIds(List<Long> ids) {
		List<User> users = userRepository.findAllById(ids);
		return users.stream()
				.map(UserDto::fromEntity)
				.toList();
	}

	public void deleteUserById(Long id) throws Exception {
		User user = userRepository.findUserById(id)
				.orElseThrow(() -> new Exception("User not found"));
		userRepository.delete(user);
	}

	public Page<UserDto> getAllUsers(Pageable pageable, String keyword) {
		Page<User> userPage;
		if (keyword != null && !keyword.trim().isEmpty()) {
			userPage = userRepository.searchByKeyword(keyword.trim(), pageable);
		} else {
			userPage = userRepository.findAll(pageable);
		}

		return userPage.map(UserDto::fromEntity);
	}


}

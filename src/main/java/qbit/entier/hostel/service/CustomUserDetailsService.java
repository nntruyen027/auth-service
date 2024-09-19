package qbit.entier.hostel.service;

import lombok.RequiredArgsConstructor;
import qbit.entier.hostel.entity.User;
import qbit.entier.hostel.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
    private UserRepository userRepository;
   
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm kiếm user trong database
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Tạo đối tượng UserDetails
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(() -> "ROLE_" + user.getRole())  
        );
    }
    
    public User createUser(User user) {
    	
    	User currentUser = userRepository.findByUsername(user.getUsername())
    			.orElse(null);
    	if(currentUser!=null) {
    		throw new RuntimeException("User already exists: " + user.getUsername());
    	}
    	
    	try {
    		user.setPassword(encoder.encode(user.getPassword()));
        	User newUser = userRepository.save(user);
        	return newUser;
    	}
    	catch (Exception e) {
			throw new RuntimeException(e);
		}
    
    }
}

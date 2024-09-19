package qbit.entier.hostel.controller;

import lombok.RequiredArgsConstructor;
import qbit.entier.hostel.dto.LoginRequest;
import qbit.entier.hostel.dto.LoginResponse;
import qbit.entier.hostel.entity.User;
import qbit.entier.hostel.service.CustomUserDetailsService;
import qbit.entier.hostel.util.JwtUtil;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService service;
    
    
    @PostMapping("/register")
    public User createOne(@RequestBody User request) {
    	try {    		
    		return service.createUser(request);
    	}
    	catch (RuntimeException e) {
    		throw new RuntimeException("error: " + e);
		}
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials"));
        }

        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @GetMapping("/test")
    public String test() {
    	return "Test thanh cong";
    }
}

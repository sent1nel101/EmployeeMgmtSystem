package com.dmcdesigns.capstone.Controllers;

import com.dmcdesigns.capstone.DTOs.JwtResponse;
import com.dmcdesigns.capstone.DTOs.LoginRequest;
import com.dmcdesigns.capstone.DTOs.MessageResponse;
import com.dmcdesigns.capstone.DTOs.RegisterRequest;
import com.dmcdesigns.capstone.DTOs.ForgotPasswordRequest;
import com.dmcdesigns.capstone.DTOs.ResetPasswordRequest;
import com.dmcdesigns.capstone.DTOs.PasswordResetResponse;
import com.dmcdesigns.capstone.Entities.Admin;
import com.dmcdesigns.capstone.Entities.Employee;
import com.dmcdesigns.capstone.Entities.Manager;
import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import com.dmcdesigns.capstone.Security.CustomUserDetailsService;
import com.dmcdesigns.capstone.Security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
                (CustomUserDetailsService.CustomUserPrincipal) userDetails;
            User user = userPrincipal.getUser();

            String role = determineUserRole(user);

            return ResponseEntity.ok(new JwtResponse(jwt, 
                                                   user.getUsername(),
                                                   user.getEmail(),
                                                   role,
                                                   user.getFirstName(),
                                                   user.getLastName()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user based on user type
        User user;
        String userType = registerRequest.getUserType().toUpperCase();
        
        switch (userType) {
            case "ADMIN":
                user = new Admin(registerRequest.getFirstName(),
                               registerRequest.getLastName(),
                               registerRequest.getEmail(),
                               registerRequest.getPhoneNumber(),
                               registerRequest.getUsername(),
                               passwordEncoder.encode(registerRequest.getPassword()),
                               registerRequest.getDepartment());
                break;
            case "MANAGER":
                user = new Manager(registerRequest.getFirstName(),
                                 registerRequest.getLastName(),
                                 registerRequest.getEmail(),
                                 registerRequest.getPhoneNumber(),
                                 registerRequest.getUsername(),
                                 passwordEncoder.encode(registerRequest.getPassword()),
                                 registerRequest.getDepartment());
                break;
            case "EMPLOYEE":
            default:
                user = new Employee(registerRequest.getFirstName(),
                                  registerRequest.getLastName(),
                                  registerRequest.getEmail(),
                                  registerRequest.getPhoneNumber(),
                                  registerRequest.getUsername(),
                                  passwordEncoder.encode(registerRequest.getPassword()),
                                  registerRequest.getDepartment());
                break;
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail());
            if (user == null) {
                // For security, don't reveal if email exists or not, but provide helpful info
                return ResponseEntity.ok(new PasswordResetResponse("If an account with this email exists, a password reset link has been generated. For demo purposes, please use a valid test account email ending with @ourcompany.com", "demo-invalid-email"));
            }

            // Generate a simple token (in production, use UUID or JWT with expiration)
            String resetToken = "reset_" + user.getId() + "_" + System.currentTimeMillis();
            
            // In a real application, you would:
            // 1. Store this token in database with expiration time
            // 2. Send email with reset link
            // For demo purposes, we'll just return the token
            
            return ResponseEntity.ok(new PasswordResetResponse("Password reset token generated", resetToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error processing password reset request"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // Check for demo invalid email token
            if ("demo-invalid-email".equals(request.getToken())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Please use a valid test account email ending with @ourcompany.com"));
            }
            
            // Extract user ID from token (simplified for demo)
            if (request.getToken() == null || !request.getToken().startsWith("reset_")) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid reset token"));
            }

            String[] tokenParts = request.getToken().split("_");
            
            if (tokenParts.length < 3) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid reset token format"));
            }

            Integer userId;
            long timestamp;
            
            try {
                userId = Integer.parseInt(tokenParts[1]);
                timestamp = Long.parseLong(tokenParts[2]);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid token format"));
            }
            
            // Check if token is expired (24 hours for demo)
            long currentTime = System.currentTimeMillis();
            long twentyFourHours = 24 * 60 * 60 * 1000;
            if (currentTime - timestamp > twentyFourHours) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Reset token has expired"));
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid user"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error resetting password"));
        }
    }

    private String determineUserRole(User user) {
        if (user instanceof Admin) {
            return "ADMIN";
        } else if (user instanceof Manager) {
            return "MANAGER";
        } else if (user instanceof Employee) {
            return "EMPLOYEE";
        }
        return "USER";
    }
}

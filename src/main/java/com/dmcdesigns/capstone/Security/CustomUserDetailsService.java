package com.dmcdesigns.capstone.Security;

import com.dmcdesigns.capstone.Entities.User;
import com.dmcdesigns.capstone.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security authentication.
 * Handles user loading by username or email and provides role-based authorities.
 * 
 * @author DMC Designs
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user by username or email for authentication.
     * First attempts to find by username, then by email for frontend compatibility.
     * 
     * @param username The username or email to search for
     * @return UserDetails object for authentication
     * @throws UsernameNotFoundException If user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }

        return new CustomUserPrincipal(user);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new CustomUserPrincipal(user);
    }

    /**
     * Custom UserPrincipal that wraps the User entity for Spring Security.
     * Provides role-based authorities based on the user's class type.
     */
    public static class CustomUserPrincipal implements UserDetails {
        private User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        /**
         * Determines user authorities based on the user's class type (Admin, Manager, Employee).
         * 
         * @return Collection of granted authorities
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            String role = "ROLE_USER";
            if (user.getClass().getSimpleName().equals("Admin")) {
                role = "ROLE_ADMIN";
            } else if (user.getClass().getSimpleName().equals("Manager")) {
                role = "ROLE_MANAGER";
            } else if (user.getClass().getSimpleName().equals("Employee")) {
                role = "ROLE_EMPLOYEE";
            }
            
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        public User getUser() {
            return user;
        }
    }
}

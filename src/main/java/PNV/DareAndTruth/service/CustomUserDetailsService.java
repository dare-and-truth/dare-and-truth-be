package PNV.DareAndTruth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import PNV.DareAndTruth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new User(user.getEmail(), user.getPassword(), user.getAuthorities()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new User(user.getEmail(), user.getPassword(), user.getAuthorities()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}

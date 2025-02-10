package PNV.DareAndTruth.service;

import java.util.*;
import PNV.DareAndTruth.dto.request.auth.LoginRequest;
import PNV.DareAndTruth.security.JwtTokenProvider;
import PNV.DareAndTruth.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.UserMapper;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    UserDetailsService userDetailsService;

    public void createUser(SignUpRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
            User user = userMapper.signUpRequestToUser(request);

            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsAdmin(false);

            userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID);
        }

        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public void updateUser(String id, UpdateUserRequest request) {
        User existingUser = getUserById(id);

        userMapper.mapUserFromUpdateUserRequest(existingUser, request);
        userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        User existingUser = getUserById(id);

        existingUser.setIsDeleted(true);
        userRepository.save(existingUser);
    }

    public Map<String, Object> authenticateUser(LoginRequest request) {

        if (!request.getEmail().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (request.getPassword().trim().isEmpty()) {
            throw new AppException(ErrorCode.PASSWORD_REQUIRED);
        }

        if (request.getPassword().trim().isBlank()) {
            throw new AppException(ErrorCode.PASSWORD_REQUIRED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "admin" : "user";

        String accessToken = jwtTokenProvider.createToken(user.getEmail(), role, false);
        String refreshToken = jwtTokenProvider.createToken(user.getEmail(), role, true);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        response.put("user", Map.of("id", user.getId(), "username", user.getUsername()));

        return response;
    }
}

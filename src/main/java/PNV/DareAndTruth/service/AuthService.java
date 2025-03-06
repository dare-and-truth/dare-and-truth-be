package PNV.DareAndTruth.service;

import java.util.HashMap;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.request.auth.SigninRequest;
import PNV.DareAndTruth.dto.response.auth.SigninResponse;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.UserMapper;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;

    static final String ACCESS_TOKEN = "access_token";
    static final String REFRESH_TOKEN = "refresh_token";

    public SigninResponse authenticateUser(SigninRequest request) {
        var authResult = validateUserCredentials(request);

        String accessToken = (String) authResult.get(ACCESS_TOKEN);
        String refreshToken = (String) authResult.get(REFRESH_TOKEN);
        Object userInfo = authResult.get("user");

        return new SigninResponse(accessToken, refreshToken, userInfo);
    }

    public Map<String, Object> validateUserCredentials(SigninRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT, HttpStatus.UNAUTHORIZED);
        }

        String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "admin" : "user";

        String accessToken = jwtService.createToken(user.getEmail(), role, false);
        String refreshToken = jwtService.createToken(user.getEmail(), role, true);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("avatar_url", user.getAvatarUrl());
        response.put("user", userInfo);
        response.put(ACCESS_TOKEN, accessToken);
        response.put(REFRESH_TOKEN, refreshToken);
        return response;
    }

    @Transactional
    public Map<String, Object> refreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken()) || !jwtService.isRefreshToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.createToken(
                user.getEmail(), Boolean.TRUE.equals(user.getIsAdmin()) ? "admin" : "user", false);

        Map<String, Object> response = new HashMap<>();
        response.put(ACCESS_TOKEN, newAccessToken);
        response.put(REFRESH_TOKEN, refreshToken);
        return response;
    }

    @Transactional
    public void logoutUser(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (JwtService.isTokenInvalid(token)) {
            throw new AppException(ErrorCode.TOKEN_ALREADY_INVALID, HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractEmail(token);

        jwtService.invalidateToken(token);

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }
}

package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.user.UserDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserSummaryProjection;
import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
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
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public void createUser(SignUpRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        // check is admin if it is the first account
        Boolean isAdmin = userRepository.count() == 0;
        if (existingUser.isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS, HttpStatus.CONFLICT);
        }
        User user = userMapper.signUpRequestToUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsAdmin(isAdmin);

        userRepository.save(user);
    }

    public Set<UserSummaryProjection> getAllUsers() {
        return userRepository.findAllByIsDeletedFalse();
    }

    public User getUserById(String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        return userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public UserDetailProjection getUserDetailById(String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        return userRepository
                .findDetailById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
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
}

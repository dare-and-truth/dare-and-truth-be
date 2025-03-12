package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.user.UserSummaryProjection;
import PNV.DareAndTruth.dto.request.auth.SignUpRequest;
import PNV.DareAndTruth.dto.request.user.UpdateUserRequest;
import PNV.DareAndTruth.dto.response.user.UserWithTypeOfRequest;
import PNV.DareAndTruth.entity.Request;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.mapper.UserMapper;
import PNV.DareAndTruth.repository.RequestRepository;
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
    ScoreService scoreService;
    RequestRepository requestRepository;

    private UUID getUserIdFromEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

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

    public User getUserById(UUID userId) {
        return userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public Object getUserDetailById(String id, String userEmail) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        UUID currentUserId = getUserIdFromEmail(userEmail);

        if (userId.equals(currentUserId)) {
            return userRepository
                    .findDetailById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        } else {
            return userRepository
                    .findBasicDetailById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
    }

    public void updateUser(String userEmail, UpdateUserRequest request, String id) {
        log.info("Updated request in DB: {}", request.getAvatarUrl());
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        UUID currentUserId = getUserIdFromEmail(userEmail);

        User currentUser = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!userId.equals(currentUserId) && (currentUser.getIsAdmin() == null || !currentUser.getIsAdmin())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        User existingUser = getUserById(userId);

        if (request.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(request.getAvatarUrl());
        }

        userMapper.mapUserFromUpdateUserRequest(existingUser, request);
        log.info("Updated avatarUrl in DB: {}", existingUser.getAvatarUrl());
        userRepository.save(existingUser);
    }

    public void deleteUser(String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        User existingUser = getUserById(userId);

        existingUser.setIsDeleted(true);
        userRepository.save(existingUser);
    }

    public UserWithTypeOfRequest getUserWithTypeOfRequest(String loggedInUserEmail, String targetUserId) {
        // Convert targetUserId to UUID
        UUID friendId;
        try {
            friendId = UUID.fromString(targetUserId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        // Retrieve the logged-in user's id
        UUID loggedInUserId = userRepository
                .findByEmail(loggedInUserEmail)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Prevent checking your own friend request status
        if (friendId.equals(loggedInUserId)) {
            throw new AppException(ErrorCode.FAIL_TO_CHECK_REQUEST_YOURSELF, HttpStatus.BAD_REQUEST);
        }

        // Retrieve target user (friend) entity from repository
        User friendUser = userRepository
                .findByIdAndIsDeletedFalse(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User loggedInUser = userRepository
                .findByIdAndIsDeletedFalse(loggedInUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Now check for requests in both roles:
        // 1. Check if the logged-in user is the recipient (i.e. request.user equals loggedInUser) and request is
        // pending.
        Optional<Request> requestAsRecipient = requestRepository.findByUserAndFollower(loggedInUser, friendUser);
        // 2. Check if the logged-in user is the sender (i.e. request.follower equals loggedInUser) and request is
        // pending.
        Optional<Request> requestAsSender = requestRepository.findByUserAndFollower(friendUser, loggedInUser);

        UserWithTypeOfRequest result = new UserWithTypeOfRequest();

        // If a request exists where the logged-in user is the recipient and is not accepted, type = NeedAccept.
        if (requestAsRecipient.isPresent()
                && Boolean.FALSE.equals(requestAsRecipient.get().getIsAccepted())) {
            result.setTypeOfRequest("NeedAccept");
            result.setRequestId(requestAsRecipient.get().getId().toString());
            return result;
        }

        // If any request between the two users is accepted, then type = Friend.
        if ((requestAsRecipient.isPresent()
                        && Boolean.TRUE.equals(requestAsRecipient.get().getIsAccepted()))
                || (requestAsSender.isPresent()
                        && Boolean.TRUE.equals(requestAsSender.get().getIsAccepted()))) {
            result.setTypeOfRequest("Friend");
            result.setRequestId(null);
            return result;
        }

        // If a request exists where the logged-in user is the sender and is still pending, type = WaitingForAccept.
        if (requestAsSender.isPresent()
                && Boolean.FALSE.equals(requestAsSender.get().getIsAccepted())) {
            result.setTypeOfRequest("WaitingForAccept");
            result.setRequestId(requestAsSender.get().getId().toString());
            return result;
        }

        // If no request exists between the users, you might choose to return a default type or throw an error.
        result.setTypeOfRequest("Stranger");
        result.setRequestId(null);
        return result;
    }
}

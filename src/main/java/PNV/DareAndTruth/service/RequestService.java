package PNV.DareAndTruth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.dto.request.request.CreateRequestRequest;
import PNV.DareAndTruth.entity.Request;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.RequestRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestService {

    final UserRepository userRepository;
    final RequestRepository requestRepository;

    // Find the user by email and set their ID.
    // If the user is not found, throw an error.
    private UUID getUserIdFromEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void createRequest(CreateRequestRequest request, String userEmail) {
        UUID followerId = getUserIdFromEmail(userEmail);
        UUID userId = UUID.fromString(request.getUserId());

        if (userId.equals(followerId)) {
            throw new AppException(ErrorCode.CANNOT_ADD_SELF, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User follower = userRepository
                .findByIdAndIsDeletedFalse(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request mutualRequest =
                requestRepository.findByUserAndFollower(follower, user).orElse(null);

        if (mutualRequest != null) {
            if (Boolean.TRUE.equals(mutualRequest.getIsAccepted())) {
                throw new AppException(ErrorCode.ALREADY_FRIENDS, HttpStatus.CONFLICT);
            } else {
                mutualRequest.setIsAccepted(true);
                mutualRequest.setAcceptedAt(LocalDateTime.now());
                requestRepository.save(mutualRequest);
                return;
            }
        }

        Request existingRequest =
                requestRepository.findByUserAndFollower(user, follower).orElse(null);
        if (existingRequest != null) {
            if (Boolean.TRUE.equals(existingRequest.getIsAccepted())) {
                throw new AppException(ErrorCode.ALREADY_FRIENDS, HttpStatus.CONFLICT);
            } else {
                throw new AppException(ErrorCode.ADD_FRIEND_REQUEST_EXIST, HttpStatus.CONFLICT);
            }
        }

        Request newRequest = Request.builder()
                .user(user)
                .follower(follower)
                .followedAt(LocalDateTime.now())
                .isAccepted(false)
                .build();
        requestRepository.save(newRequest);
    }

    @Transactional(readOnly = true)
    public List<FriendDetailProjection> getAllAddFriendRequests(String userEmail) {
        UUID existingUserId = getUserIdFromEmail(userEmail);

        if (!userRepository.existsByIdAndIsDeletedFalse(existingUserId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return requestRepository.findAllByUserIdOrFollowerIdAndIsAcceptedFalse(existingUserId, existingUserId);
    }

    @Transactional(readOnly = true)
    public List<FriendDetailProjection> getAllFriendsList(String userEmail) {
        UUID existingUserId = getUserIdFromEmail(userEmail);

        if (!userRepository.existsByIdAndIsDeletedFalse(existingUserId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return requestRepository.findAllByUserIdOrFollowerIdAndIsAcceptedTrue(existingUserId, existingUserId);
    }

    @Transactional
    public void acceptRequest(UUID requestId, String userEmail) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        UUID existingUserId = getUserIdFromEmail(userEmail);

        if (!request.getUser().getId().equals(existingUserId)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(request.getIsAccepted())) {
            throw new AppException(ErrorCode.ALREADY_FRIENDS, HttpStatus.CONFLICT);
        }

        request.setIsAccepted(true);
        request.setAcceptedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(UUID requestId, String userEmail) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        UUID existingUserId = getUserIdFromEmail(userEmail);

        if (!request.getUser().getId().equals(existingUserId)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        requestRepository.delete(request);
    }

    @Transactional
    public void unfFriend(String userEmail, UUID friendId) {
        UUID existingUserId = getUserIdFromEmail(userEmail);

        User user = userRepository
                .findByIdAndIsDeletedFalse(existingUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User friend = userRepository
                .findByIdAndIsDeletedFalse(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request request = requestRepository
                .findByUserAndFollower(user, friend)
                .or(() -> requestRepository.findByUserAndFollower(friend, user))
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (Boolean.FALSE.equals(request.getIsAccepted())) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_NOT_ACCEPTED, HttpStatus.BAD_REQUEST);
        }

        requestRepository.delete(request);
    }
}

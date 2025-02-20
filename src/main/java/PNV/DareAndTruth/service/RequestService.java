package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.request.request.CreateRequestRequest;
import PNV.DareAndTruth.dto.response.request.RequestResponse;
import PNV.DareAndTruth.dto.response.user.UserResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestService {

    UserRepository userRepository;
    RequestRepository requestRepository;

    @Transactional
    public void createRequest(CreateRequestRequest request, String userEmail) {
        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        UUID followerId = exitingUser.get().getId();
        UUID userId = UUID.fromString(request.getUserId());

        if (userId.equals(followerId)) {
            throw new AppException(ErrorCode.CANNOT_ADD_SELF, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User follower = userRepository.findByIdAndIsDeletedFalse(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request mutualRequest = requestRepository.findByUserAndFollower(follower, user).orElse(null);

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

        Request existingRequest = requestRepository.findByUserAndFollower(user, follower).orElse(null);
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
    public List<RequestResponse> getAllRequests(String userEmail) {

        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        UUID userId = exitingUser.get().getId();

        if (!userRepository.existsByIdAndIsDeletedFalse(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<Request> requests = requestRepository.findAllByUserIdOrFollowerId(userId, userId);

        return requests.stream()
                .map(request -> new RequestResponse(
                        request.getId(),
                        request.getFollowedAt(),
                        request.getIsAccepted(),
                        request.getAcceptedAt(),
                        new UserResponse(request.getUser().getId(), request.getUser().getUsername()),
                        new UserResponse(request.getFollower().getId(), request.getFollower().getUsername())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptRequest(UUID requestId, String userEmail) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        UUID userId = exitingUser.get().getId();

        if (!request.getUser().getId().equals(userId)) {
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
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        UUID userId = exitingUser.get().getId();

        if (!request.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        requestRepository.delete(request);
    }

    // delete request when: user clicks no accept (above), when unfriending (below)
    @Transactional
    public void deleteFriend(String userEmail, UUID friendId) {
        Optional<User> exitingUser = userRepository.findByEmail(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        UUID userId = exitingUser.get().getId();

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User friend = userRepository.findByIdAndIsDeletedFalse(friendId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request request = requestRepository.findByUserAndFollower(user, friend)
                .or(() -> requestRepository.findByUserAndFollower(friend, user))
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (Boolean.FALSE.equals(request.getIsAccepted())) {
            throw new AppException(ErrorCode.FRIEND_REQUEST_NOT_ACCEPTED, HttpStatus.BAD_REQUEST);
        }

        requestRepository.delete(request);
    }
}
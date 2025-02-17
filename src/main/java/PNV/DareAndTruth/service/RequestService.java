package PNV.DareAndTruth.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestService {

    UserRepository userRepository;
    RequestRepository requestRepository;

    @Transactional
    public void createRequest(CreateRequestRequest request) {
        UUID userId = UUID.fromString(request.getUserId());
        UUID followerId = UUID.fromString(request.getFollowerId());

        if (userId.equals(followerId)) {
            throw new AppException(ErrorCode.CANNOT_ADD_SELF, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndIsDeletedFalse(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User follower = userRepository.findByIdAndIsDeletedFalse(UUID.fromString(request.getFollowerId()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request mutualRequest = requestRepository.findByUserAndFollower(follower, user).orElse(null);

        if (mutualRequest != null) {
            if (Boolean.TRUE.equals(mutualRequest.getIsAccepted())) {
                throw new AppException(ErrorCode.ALREADY_FRIENDS, HttpStatus.CONFLICT); // Đã là bạn bè
            } else {
                mutualRequest.setIsAccepted(true);
                requestRepository.save(mutualRequest);
                return;
            }
        }

        Request existingRequest = requestRepository.findByUserAndFollower(user, follower).orElse(null);
        if (existingRequest != null) {
            if (Boolean.TRUE.equals(existingRequest.getIsAccepted())) {
                throw new AppException(ErrorCode.ALREADY_FRIENDS, HttpStatus.CONFLICT); // Đã là bạn bè
            } else {
                throw new AppException(ErrorCode.ADD_FRIEND_REQUEST_EXIST, HttpStatus.CONFLICT); // Lời mời đã tồn tại
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
    public List<Request> getAllRequests(String userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        return requestRepository.findAllByUser(user); // Ensure this query exists in your repository
    }

    @Transactional
    public void acceptRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        request.setIsAccepted(true);
        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        requestRepository.delete(request);
    }

    @Transactional
    public void deleteFriend(UUID userId, UUID followerId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        User follower = userRepository.findByIdAndIsDeletedFalse(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Request request = requestRepository.findByUserAndFollower(user, follower)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND, HttpStatus.NOT_FOUND));

        requestRepository.delete(request);
    }
}

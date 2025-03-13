package PNV.DareAndTruth.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import PNV.DareAndTruth.mapper.FeedMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.dto.response.feed.FeedResponse;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.FeedRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    FeedRepository feedRepository;
    UserRepository userRepository;
    FeedMapper feedMapper;

    public User getUserById(UUID userId) {
        return userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private UUID getUserIdFromEmail(String userEmail) {
        return userRepository
                .findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public List<GetFeedResponse> getFeed(int page, int size, String userEmail) {

        Optional<UserWithIdProjection> exitingUser = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        int offset = page * size;
        List<Object[]> results =
                feedRepository.getFeedWithCounts(exitingUser.get().getId(), size, offset);

        return results.stream()
                .map(row -> new GetFeedResponse(
                        (UUID) row[0], // ID
                        (String) row[1], // Type (post/challenge)
                        (String) row[2], // Hashtag
                        (String) row[3], // Content
                        (String) row[4], // Media URL
                        row[5] != null ? row[5].toString() : null, // Start Date (for challenge)
                        row[6] != null ? row[6].toString() : null, // End Date (for
                        ((Timestamp) row[7]).toLocalDateTime(),
                        (UUID) row[8], // User ID
                        (String) row[9], // Username
                        (String) row[10],
                        ((Number) row[11]).intValue(), // Like Count
                        ((Number) row[12]).intValue(), // Comment Count
                        (Boolean) row[13],
                        (Boolean) row[14]))
                .toList();
    }

    public List<GetFeedResponse> getFeedByUser(String id, String type, int page, int size) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }
        Optional<User> exitingUser = userRepository.findByIdAndIsDeletedFalse(userId);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        int offset = page * size;
        List<Object[]> results =
                feedRepository.getFeedWithCountsAndType(exitingUser.get().getId(), size, offset, type);

        return results.stream()
                .map(row -> new GetFeedResponse(
                        (UUID) row[0], // ID
                        (String) row[1], // Type (post/challenge)
                        (String) row[2], // Hashtag
                        (String) row[3], // Content
                        (String) row[4], // Media URL
                        row[5] != null ? row[5].toString() : null, // Start Date (for challenge)
                        row[6] != null ? row[6].toString() : null, // End Date (for
                        ((Timestamp) row[7]).toLocalDateTime(),
                        (UUID) row[8], // User ID
                        (String) row[9], // Username
                        (String) row[10],
                        ((Number) row[11]).intValue(), // Like Count
                        ((Number) row[12]).intValue(), // Comment Count
                        (Boolean) row[13],
                        (Boolean) row[14]))
                .toList();
    }

    public FeedResponse getFeedById(String id, String type, String userEmail) {
        UUID userId = getUserIdFromEmail(userEmail);
        return feedRepository.findFeedByIdAndType(UUID.fromString(id), type, userId);
    }

    public List<GetFeedResponse> getFeedLovedByUserId(String userId, int page, int size) {
        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        Optional<User> existingUser = userRepository.findByIdAndIsDeletedFalse(userUUID);
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        // Tính toán offset từ page và size
        int offset = page * size;

        // Gọi repository với phân trang
        List<Object[]> results = feedRepository.getFeedsLovedByUser(userUUID, size, offset);

        // Chuyển đổi kết quả từ query thành danh sách GetFeedResponse
        return results.stream()
                .map(row -> new GetFeedResponse(
                        (UUID) row[0], // ID
                        (String) row[1], // Type (post/challenge)
                        (String) row[2], // Hashtag
                        (String) row[3], // Content
                        (String) row[4], // Media URL
                        row[5] != null ? row[5].toString() : null, // Start Date (for challenge)
                        row[6] != null ? row[6].toString() : null, // End Date (for challenge)
                        ((Timestamp) row[7]).toLocalDateTime(), // Created At
                        (UUID) row[8], // User ID
                        (String) row[9], // Username
                        (String) row[10], // Avatar URL
                        ((Number) row[11]).intValue(), // Like Count
                        ((Number) row[12]).intValue(), // Comment Count
                        (Boolean) row[13], // is_like
                        (Boolean) row[14]// is_joined
                ))
                .toList();
    }

    public Map<String, Object> getFeedDetailByHashtagAndDate(
            String hashtag, String startDate, String endDate, int page, int size) {

        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        int offset = page * size;

        // Lấy danh sách bài viết
        List<Object[]> results = feedRepository.getFeedDetailByHashtagAndDate(
                hashtag, startLocalDate, endLocalDate, size, offset);

        // Dùng mapper để chuyển đổi dữ liệu
        List<GetFeedResponse> feeds = results.stream()
                .map(feedMapper::mapToFeedResponse)
                .toList();

        // Lấy tổng số bài viết từ query (dữ liệu ở cột cuối cùng)
        Long totalPosts = results.isEmpty() ? 0 : ((Number) results.get(0)[10]).longValue();

        // Gửi response dưới dạng JSON object chứa cả danh sách bài viết và tổng số bài viết
        Map<String, Object> response = new HashMap<>();
        response.put("feeds", feeds);
        response.put("totalPosts", totalPosts);
        return response;
    }

}

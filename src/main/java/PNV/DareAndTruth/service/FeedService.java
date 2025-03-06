package PNV.DareAndTruth.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import PNV.DareAndTruth.dto.response.feed.FeedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.FeedRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    FeedRepository feedRepository;
    UserRepository userRepository;

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
                        ((Number) row[10]).intValue(), // Like Count
                        ((Number) row[11]).intValue(), // Comment Count
                        (Boolean) row[12],
                        (Boolean) row[13]))
                .toList();
    }

    public List<GetFeedResponse> getFeedByUser(String id, String type, int page, int size, String userEmail) {
        UUID userId;
        if (id != null) {
            try {
                userId = UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.USER_ID_INVALID, HttpStatus.BAD_REQUEST);
            }
        } else {
            userId = getUserIdFromEmail(userEmail);
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
                        ((Number) row[10]).intValue(), // Like Count
                        ((Number) row[11]).intValue(), // Comment Count
                        (Boolean) row[12],
                        (Boolean) row[13]))
                .toList();
    }

    public FeedResponse getFeedById(String id, String type, String userEmail) {
        UUID userId = getUserIdFromEmail(userEmail);
        return feedRepository.findFeedByIdAndType(UUID.fromString(id), type, userId);
    }
}

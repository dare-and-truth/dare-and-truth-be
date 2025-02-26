package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.FeedRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    FeedRepository feedRepository;
    UserRepository userRepository;

    public List<GetFeedResponse> getFeed(int page, int size, String userEmail) {

        Optional<UserWithIdProjection> exitingUser = userRepository.findByEmailAndIsDeletedFalse(userEmail);
        if (exitingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        int offset = page * size;
        List<Object[]> results = feedRepository.getFeedWithCounts(exitingUser.get().getId(),size, offset);

        return results.stream().map(row -> new GetFeedResponse(
                (UUID) row[0],  // ID
                (String) row[1], // Type (post/challenge)
                (String) row[2], // Hashtag
                (String) row[3], // Content
                (String) row[4], // Media URL
                row[5] != null ? row[5].toString() : null, // Start Date (for challenge)
                row[6] != null ? row[6].toString() : null, // End Date (for
                ((Timestamp) row[7]).toLocalDateTime(),
                (UUID) row[8],  // User ID
                (String) row[9], // Username
                ((Number) row[10]).intValue(), // Like Count
                ((Number) row[11]).intValue(),  // Comment Count
                (Boolean) row[12]
        )).toList();
    }
}

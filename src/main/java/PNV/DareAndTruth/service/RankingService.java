package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
import PNV.DareAndTruth.dto.response.ranking.UserRankingWithScoreResponse;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RankingService {
    PostRepository postRepository;
    ChallengeRepository challengeRepository;
    UserRepository userRepository;
    ReminderRepository reminderRepository;

    public List<UserRankingResponse> getRankingOfChallenge(String challengeId) {
        UUID currentChallengeId = UUID.fromString(challengeId);

        var challenge = challengeRepository
                .findByIdAndIsDeletedFalse(currentChallengeId)
                .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));

        String hashtag = challenge.getHashtag();
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();

        // 1. Lấy danh sách user có bài post và tổng số like từ PostRepository
        List<Object[]> rankingData = postRepository.getPostByHashtagAndDate(hashtag, startDate, endDate);

        // 2. Lưu vào Map (userId -> UserRankingResponse)
        Map<UUID, UserRankingResponse> rankingMap = new HashMap<>();
        for (Object[] row : rankingData) {
            UUID userId = (UUID) row[0];
            String username = (String) row[1];
            String avatarUrl = (String) row[2];
            int totalLikes = row[3] != null ? ((Number) row[3]).intValue() : 0;

            rankingMap.put(userId, new UserRankingResponse(userId, username, avatarUrl, totalLikes, 0));
        }

        // 3. Lấy danh sách user từ Reminder (có thể chưa có post)
        List<UUID> reminderUserIds = reminderRepository.findUserIdsByHashtagAndDateRange(hashtag, startDate, endDate);

        for (UUID userId : reminderUserIds) {
            if (!rankingMap.containsKey(userId)) { // Nếu user chưa có trong ranking (chưa post)
                var user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    rankingMap.put(userId, new UserRankingResponse(userId, user.getUsername(), user.getAvatarUrl(), 0, 0));
                }
            }
        }

        // 4. Chuyển Map -> List, sắp xếp theo tổng like giảm dần
        List<UserRankingResponse> rankings = new ArrayList<>(rankingMap.values());
        rankings.sort(Comparator.comparingInt(UserRankingResponse::getTotalLikes).reversed());

        // 5. Gán thứ hạng
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }


    public List<UserRankingWithScoreResponse> getRankingOfServer() {
        List<Object[]> userScores = userRepository.getAllUsersWithScores();

        List<UserRankingWithScoreResponse> rankings = userScores.stream()
                .map(row -> new UserRankingWithScoreResponse(
                        (UUID) row[0], // userId
                        (String) row[1], // username
                        (String) row[2], // avatarUrl
                        row[3] != null ? ((Number) row[3]).intValue() : 0, // totalScore
                        0 // rank (sẽ set sau)
                        ))
                .sorted(Comparator.comparingInt(UserRankingWithScoreResponse::getTotalScore)
                        .reversed())
                .collect(Collectors.toList());

        // Gán rank
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }
}

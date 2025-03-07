package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
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

    public List<UserRankingResponse> getRankingOfChallenge(String challengeId) {
        UUID currentChallengeId = UUID.fromString(challengeId);

        // Lấy thông tin challenge
        var challenge = challengeRepository
                .findByIdAndIsDeletedFalse(currentChallengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        String hashtag = challenge.getHashtag();
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();

        // Gọi query để lấy danh sách user và tổng số lượt like của họ
        List<Object[]> rankingData = postRepository.getPostByHashtagAndDate(hashtag, startDate, endDate);

        // Chuyển đổi danh sách Object[] thành danh sách UserRankingResponse
        List<UserRankingResponse> rankings = rankingData.stream()
                .map(row -> new UserRankingResponse(
                        (UUID) row[0], // userId
                        (String) row[1], // username
                        (String) row[2], // avatarUrl
                        ((Number) row[3]).intValue(), // totalLikes
                        0 // rank (sẽ set sau)
                        ))
                .sorted(Comparator.comparingInt(UserRankingResponse::getTotalLikes)
                        .reversed()) // Sắp xếp theo totalLikes giảm dần
                .collect(Collectors.toList());

        // Gán rank
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }

    public List<UserRankingResponse> getRankingOfServer() {
        // Lấy danh sách tất cả người dùng
        List<Object[]> userScores = userRepository.getAllUsersWithScores();

        // Chuyển đổi danh sách Object[] thành danh sách UserRankingResponse
        List<UserRankingResponse> rankings = userScores.stream()
                .map(row -> new UserRankingResponse(
                        (UUID) row[0], // userId
                        (String) row[1], // username
                        (String) row[2], // avatarUrl
                        row[3] != null ? ((Number) row[3]).intValue() : 0, // totalScore (nếu null thì mặc định 0)
                        0 // rank (sẽ set sau)
                        ))
                .sorted(Comparator.comparingInt(UserRankingResponse::getTotalLikes)
                        .reversed()) // Sắp xếp theo totalScore giảm dần
                .collect(Collectors.toList());

        // Gán rank
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }
}

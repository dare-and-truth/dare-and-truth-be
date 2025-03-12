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

    public List<UserRankingResponse> getRankingOfChallenge(String challengeId) {
        UUID currentChallengeId = UUID.fromString(challengeId);

        var challenge = challengeRepository
                .findByIdAndIsDeletedFalse(currentChallengeId)
                .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));

        String hashtag = challenge.getHashtag();
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();

        List<Object[]> rankingData = postRepository.getPostByHashtagAndDate(hashtag, startDate, endDate);

        List<UserRankingResponse> rankings = rankingData.stream()
                .map(row -> new UserRankingResponse(
                        (UUID) row[0], // userId
                        (String) row[1], // username
                        (String) row[2], // avatarUrl
                        row[3] != null ? ((Number) row[3]).intValue() : 0, // totalLikes
                        0 // rank
                        ))
                .sorted(Comparator.comparingInt(UserRankingResponse::getTotalLikes)
                        .reversed())
                .collect(Collectors.toList());

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

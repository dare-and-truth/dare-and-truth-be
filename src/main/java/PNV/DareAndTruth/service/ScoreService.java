package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreService {
    ReminderRepository reminderRepository;
    ScoreRepository scoreRepository;
    ChallengeRepository challengeRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    RankingService rankingService;

    public ScoreSummaryProjection calculateTotalScoreForUser(UUID userId) {
        return scoreRepository
                .findTotalScoreByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SCORES_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void calculateAndSaveChallengeScores() {
        List<Challenge> remindedChallenges = reminderRepository.findEndedChallengesInReminder();

        for (Challenge challenge : remindedChallenges) {
            processChallengeCreatorScore(challenge);
            processRankingScores(challenge);
        }
    }

    private void processChallengeCreatorScore(Challenge challenge) {
        LocalDate startDate = challenge.getStartDate();
        LocalDate endDate = challenge.getEndDate();

        int participantCount = reminderRepository.countParticipantsByHashtagAndDateRange(
                challenge.getHashtag(), startDate, endDate);

        boolean exists = scoreRepository.existsByUserAndChallengeAndScoreType(challenge.getUser(), challenge, 4);
        if (!exists && participantCount > 0) {
            int challengeScore = calculateChallengeScore(participantCount);

            scoreRepository.save(Score.builder()
                    .user(challenge.getUser())
                    .scoreReceived(challengeScore)
                    .scoreType(4)
                    .challenge(challenge)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }


    private int calculateChallengeScore(int participants) {
        if (participants >= 10000) return 100;
        if (participants >= 100) return 50;
        if (participants >= 1) return 30;
        return 10;
    }

    private void processRankingScores(Challenge challenge) {
        List<UserRankingResponse> rankings = rankingService.getRankingOfChallenge(challenge.getId().toString());

        for (UserRankingResponse ranking : rankings) {
            int score = switch (ranking.getRank()) {
                case 1 -> 100;
                case 2 -> 70;
                case 3 -> 50;
                default -> 30;
            };

            userRepository.findById(ranking.getUserId()).ifPresent(user -> {
                boolean scoreExists = scoreRepository.existsByUserAndChallengeAndScoreType(user, challenge, 1);
                if (!scoreExists) {
                    try {
                        scoreRepository.save(Score.builder()
                                .user(user)
                                .scoreReceived(score)
                                .scoreType(1)
                                .challenge(challenge)
                                .createdAt(LocalDateTime.now())
                                .build());
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }
}

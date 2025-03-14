package PNV.DareAndTruth.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.dto.response.ranking.UserRankingResponse;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Post;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.entity.User;
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
        return scoreRepository.findTotalScoreByUserId(userId).orElseGet(() -> {
            if (!userRepository.existsByIdAndIsDeletedFalse(userId)) {
                throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            return new ScoreSummaryProjection() {
                @Override
                public UUID getUserId() {
                    return userId;
                }

                @Override
                public int getTotalScore() {
                    return 0;
                }
            };
        });
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

        int participantCount =
                reminderRepository.countParticipantsByHashtagAndDateRange(challenge.getHashtag(), startDate, endDate);

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
        List<UserRankingResponse> rankings =
                rankingService.getRankingOfChallenge(challenge.getId().toString());

        for (UserRankingResponse ranking : rankings) {
            int score =
                    switch (ranking.getRank()) {
                        case 1 -> 100;
                        case 2 -> 70;
                        case 3 -> 50;
                        default -> 30;
                    };

            userRepository.findById(ranking.getUserId()).ifPresent(user -> {
                if (scoreRepository.existsByUserAndChallengeAndScoreType(user, challenge, 1)) {
                    throw new AppException(ErrorCode.SCORE_ALREADY_EXISTS, HttpStatus.CONFLICT);
                }

                scoreRepository.save(Score.builder()
                        .user(user)
                        .scoreReceived(score)
                        .scoreType(1)
                        .challenge(challenge)
                        .createdAt(LocalDateTime.now())
                        .build());
            });
        }
    }

    @Transactional
    public void addLikeScore(UUID feedId, String feedType, String likingUserEmail) {

        // Retrieve the user who is liking the feed
        User likingUser = userRepository
                .findByEmail(likingUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Check if the liking user is not the creator of the feed and add score if not already awarded
        if ("post".equalsIgnoreCase(feedType)) {
            Post post = postRepository
                    .findPostEntityByIdAndIsDeletedFalse(feedId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND));

            // Do not award points if the liker is the owner
            if (post.getId().equals(likingUser.getId())) {
                return;
            }

            // Check if a score of type 3 (like) already exists for this post for the liking user
            boolean scoreExists =
                    scoreRepository.existsByUser_IdAndScoreTypeAndPost_Id(likingUser.getId(), 3, post.getId());

            if (!scoreExists) {
                Score score = Score.builder()
                        .user(likingUser)
                        .scoreReceived(1)
                        .scoreType(3)
                        .post(post)
                        .createdAt(LocalDateTime.now())
                        .build();
                scoreRepository.save(score);
            }
        } else if ("challenge".equalsIgnoreCase(feedType)) {
            Challenge challenge = challengeRepository
                    .findChallengeEntityByIdAndIsDeletedFalse(feedId)
                    .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));

            // Do not award points if the liker is the challenge owner
            if (challenge.getUser().getId().equals(likingUser.getId())) {
                return;
            }

            boolean scoreExists = scoreRepository.existsByUser_IdAndScoreTypeAndChallenge_Id(
                    likingUser.getId(), 3, challenge.getId());

            if (!scoreExists) {
                Score score = Score.builder()
                        .user(likingUser)
                        .scoreReceived(1)
                        .scoreType(3)
                        .challenge(challenge)
                        .createdAt(LocalDateTime.now())
                        .build();
                scoreRepository.save(score);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_FEED_TYPE, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void addCommentScore(UUID feedId, String feedType, String commentingUserEmail) {
        User commentingUser = userRepository
                .findByEmail(commentingUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if ("post".equalsIgnoreCase(feedType)) {
            Post post = postRepository
                    .findPostEntityByIdAndIsDeletedFalse(feedId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (post.getUser().getId().equals(commentingUser.getId())) {
                return;
            }

            boolean scoreExists =
                    scoreRepository.existsByUser_IdAndScoreTypeAndPost_Id(commentingUser.getId(), 5, post.getId());
            if (!scoreExists) {
                Score score = Score.builder()
                        .user(commentingUser)
                        .scoreReceived(2)
                        .scoreType(5)
                        .post(post)
                        .createdAt(LocalDateTime.now())
                        .build();
                scoreRepository.save(score);
            }
        } else if ("challenge".equalsIgnoreCase(feedType)) {
            Challenge challenge = challengeRepository
                    .findChallengeEntityByIdAndIsDeletedFalse(feedId)
                    .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));

            if (challenge.getUser().getId().equals(commentingUser.getId())) {
                return;
            }

            boolean scoreExists = scoreRepository.existsByUser_IdAndScoreTypeAndChallenge_Id(
                    commentingUser.getId(), 5, challenge.getId());
            if (!scoreExists) {
                Score score = Score.builder()
                        .user(commentingUser)
                        .scoreReceived(2)
                        .scoreType(5)
                        .challenge(challenge)
                        .createdAt(LocalDateTime.now())
                        .build();
                scoreRepository.save(score);
            }
        } else {
            throw new AppException(ErrorCode.INVALID_FEED_TYPE, HttpStatus.BAD_REQUEST);
        }
    }
}

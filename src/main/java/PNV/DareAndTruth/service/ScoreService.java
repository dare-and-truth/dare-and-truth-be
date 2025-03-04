package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.ScoreRepository;
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
import java.util.UUID;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreService {
    ScoreRepository scoreRepository;
    ChallengeRepository challengeRepository;
    UserRepository userRepository;

    public ScoreSummaryProjection calculateTotalScoreForUser(UUID userId) {
        return scoreRepository.findTotalScoreByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SCORES_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


    // Tính và lưu điểm cho thử thách khi kết thúc
    @Transactional
    public void calculateAndSaveChallengeScore(UUID challengeId) {
        // Lấy thông tin challenge
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Kiểm tra xem đã tính điểm cho challenge này chưa
        if (scoreRepository.existsByChallengeIdAndScoreType(challengeId, 4)) {
            return; // Nếu đã tồn tại điểm với scoreType = 4, không tính lại
        }

        // Đếm số người tham gia duy nhất
        LocalDateTime startDate = challenge.getStartDate().atStartOfDay();
        LocalDateTime endDate = challenge.getEndDate().atTime(23, 59, 59);
        long participantCount = scoreRepository.countUniqueParticipantsByHashtagAndDateRange(
                challenge.getHashtag(), startDate, endDate);

        // Tính điểm dựa trên số người tham gia
        int scoreReceived = calculateScore(participantCount);

        // Lưu điểm vào bảng scores
        Score score = Score.builder()
                .user(challenge.getUser())
                .scoreReceived(scoreReceived)
                .scoreType(4)
                .createdAt(LocalDateTime.now())
                .challenge(challenge)
                .build();

        scoreRepository.save(score);
    }

    // Phương thức tính điểm dựa trên số người tham gia
    private int calculateScore(long participantCount) {
        if (participantCount >= 10000) return 100;
        if (participantCount >= 100) return 50;
        if (participantCount >= 10) return 30;
        if (participantCount >= 1) return 10;
        return 0; // Không có người tham gia
    }
}

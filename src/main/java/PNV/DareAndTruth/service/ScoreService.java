package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ScoreRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreService {
    ScoreRepository scoreRepository;

    public ScoreSummaryProjection calculateTotalScoreForUser(UUID userId) {
        return scoreRepository.findTotalScoreByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_SCORES_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}

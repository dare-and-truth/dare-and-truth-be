package PNV.DareAndTruth.service;

import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScoreSchedulerService {
    private final ChallengeRepository challengeRepository;
    private final ScoreService scoreService;

    // Chạy mỗi ngày lúc 1 giờ sáng
//    @Scheduled(cron = "0 0 1 * * ?")
//    2h chiều
    @Scheduled(cron = "0 15 14 * * ?")
    public void calculateScoresForEndedChallenges() {
        LocalDate today = LocalDate.now();
        List<Challenge> endedChallenges = challengeRepository.findByEndDateAndIsDeletedFalse(today.minusDays(1));
        for (Challenge challenge : endedChallenges) {
            scoreService.calculateAndSaveChallengeScore(challenge.getId());
        }
    }
}
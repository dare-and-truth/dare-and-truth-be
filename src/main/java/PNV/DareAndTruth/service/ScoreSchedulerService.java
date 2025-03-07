package PNV.DareAndTruth.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreSchedulerService {
    ScoreService scoreService;

    @Scheduled(cron = "00 02 01 * * ?")
    public void scheduleChallengeScoreCalculation() {
        scoreService.calculateAndSaveChallengeScores();
    }
}

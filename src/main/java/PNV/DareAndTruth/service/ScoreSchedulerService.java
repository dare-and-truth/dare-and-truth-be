package PNV.DareAndTruth.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScoreSchedulerService {
    private final ScoreService scoreService;

    @Scheduled(cron = "0 59 23 * * ?")
    public void scheduleChallengeScoreCalculation() {
        scoreService.calculateAndSaveChallengeScores();
    }
}

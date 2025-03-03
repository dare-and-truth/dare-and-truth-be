package PNV.DareAndTruth.configuration;

import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.repository.ScoreRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DataSeeder {

    ScoreRepository scoreRepository;

    @PostConstruct
    public void seedScores() {
        if (scoreRepository.count() == 0) { // Check if table is empty
            List<Score> scores = List.of(
                    new Score("DARE", 100),
                    new Score("TRUTH", 50)
            );

            scoreRepository.saveAll(scores);
            log.info("Sample scores seeded successfully!");
        } else {
            log.info("Scores table already populated. Skipping seeding.");
        }
    }
}

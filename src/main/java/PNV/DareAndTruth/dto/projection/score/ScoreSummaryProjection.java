package PNV.DareAndTruth.dto.projection.score;

import java.util.UUID;

public interface ScoreSummaryProjection {
    UUID getUserId(); // ID of the user

    int getTotalScore(); // Total accumulated points for the user
}

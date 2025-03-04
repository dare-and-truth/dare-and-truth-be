package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
  List<Score> findByUserId(UUID userId);

  @Query("SELECT new PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection(s.user.id, SUM(s.scoreReceived)) " +
          "FROM Score s " +
          "WHERE s.user.id = :userId " +
          "GROUP BY s.user.id")
  Optional<ScoreSummaryProjection> findTotalScoreByUserId(@Param("userId") UUID userId);
}
package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Score;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
  List<Score> findByUserId(UUID userId);

  @Query("SELECT s.user.id AS userId, SUM(s.scoreReceived) AS totalScore " +
          "FROM Score s " +
          "WHERE s.user.id = :userId " +
          "GROUP BY s.user.id")
  Optional<ScoreSummaryProjection> findTotalScoreByUserId(@Param("userId") UUID userId);

  boolean existsByUserIdAndScoreTypeAndCreatedAtAfter(UUID id, int i, LocalDateTime todayStart);

  boolean existsByUserIdAndScoreTypeAndChallengeIdAndCreatedAtAfter(UUID id, int i, UUID id1, LocalDateTime todayStart);
}
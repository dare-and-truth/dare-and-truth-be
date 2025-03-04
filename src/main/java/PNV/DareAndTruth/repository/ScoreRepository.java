package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
  List<Score> findByUserId(UUID userId);

  @Query("SELECT s.user.id AS userId, SUM(s.scoreReceived) AS totalScore " +
          "FROM Score s " +
          "WHERE s.user.id = :userId " +
          "GROUP BY s.user.id")
  Optional<ScoreSummaryProjection> findTotalScoreByUserId(@Param("userId") UUID userId);

  @Query("SELECT COUNT(DISTINCT p.user.id) " +
          "FROM Post p " +
          "WHERE p.hashtag = :hashtag " +
          "AND p.createdAt BETWEEN :startDate AND :endDate " +
          "AND p.isDeleted = false")
  long countUniqueParticipantsByHashtagAndDateRange(String hashtag, LocalDateTime startDate, LocalDateTime endDate);

  boolean existsByChallengeIdAndScoreType(UUID challengeId, int i);

  // Kiểm tra xem đã tồn tại điểm cho user, challenge, và ngày với scoreType = 2 chưa
  boolean existsByUserIdAndChallengeIdAndScoreTypeAndCreatedAtDate(
          UUID userId, UUID challengeId, int scoreType, LocalDate date);

  // Đếm số Post duy nhất trong ngày của user cho một hashtag và khoảng thời gian challenge
  @Query("SELECT COUNT(DISTINCT p) " +
          "FROM Post p " +
          "WHERE p.user.id = :userId " +
          "AND p.hashtag = :hashtag " +
          "AND p.createdAt >= :startOfDay " +
          "AND p.createdAt <= :endOfDay " +
          "AND p.isDeleted = false")
  long countPostsByUserAndHashtagAndDate(
          UUID userId, String hashtag, LocalDate startOfDay, LocalDate endOfDay);
}
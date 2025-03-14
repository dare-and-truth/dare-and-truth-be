package PNV.DareAndTruth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PNV.DareAndTruth.dto.projection.score.ScoreSummaryProjection;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Score;
import PNV.DareAndTruth.entity.User;

@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    List<Score> findByUserId(UUID userId);

    @Query("SELECT s.user.id AS userId, SUM(s.scoreReceived) AS totalScore " + "FROM Score s "
            + "WHERE s.user.id = :userId "
            + "GROUP BY s.user.id")
    Optional<ScoreSummaryProjection> findTotalScoreByUserId(@Param("userId") UUID userId);

    boolean existsByUserIdAndScoreTypeAndChallengeIdAndCreatedAtAfter(
            UUID id, int i, UUID id1, LocalDateTime todayStart);

    boolean existsByUserAndChallengeAndScoreType(User user, Challenge challenge, int i);

    boolean existsByUser_IdAndScoreTypeAndPost_Id(UUID userId, int scoreType, UUID postId);
    boolean existsByUser_IdAndScoreTypeAndChallenge_Id(UUID userId, int scoreType, UUID challengeId);

    boolean existsByUserIdAndScoreTypeAndChallengeId(UUID id, int i, UUID id1);
}

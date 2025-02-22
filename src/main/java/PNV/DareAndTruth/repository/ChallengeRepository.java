package PNV.DareAndTruth.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountResponse;
import PNV.DareAndTruth.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Set<ChallengeSummaryProjection> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<ChallengeSummaryProjection> findByIdAndIsDeletedFalse(UUID uuid);

    @Query(
            """
		SELECT COUNT(c) > 0 FROM Challenge c WHERE c.hashtag = :hashtag
		AND (c.startDate BETWEEN :startDate AND :endDate
			OR c.endDate BETWEEN :startDate AND :endDate)
	""")
    boolean existsWithOverlappingDates(
            @Param("hashtag") String hashtag,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(
            """
	SELECT new PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountResponse(
		c.id, c.hashtag, c.content, c.mediaUrl,
		c.startDate, c.endDate, c.createdAt,
		c.user.id, c.user.username,
		COUNT(l.id),
		CASE WHEN COUNT(likedByUser.id) > 0 THEN true ELSE false END
	)
	FROM Challenge c
	LEFT JOIN Like l ON c.id = l.feedId
	LEFT JOIN Like likedByUser ON c.id = likedByUser.feedId AND likedByUser.user.id = :userId
	GROUP BY c.id, c.hashtag, c.content, c.mediaUrl, c.startDate, c.endDate, c.createdAt, c.user.id, c.user.username
	ORDER BY c.updatedAt DESC
""")
    List<ChallengeWithUserAndLikeCountResponse> findAllChallengesWithLikeCount(@Param("userId") UUID userId);
}

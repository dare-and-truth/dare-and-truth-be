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
import PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountAndCommentCountResponse;
import PNV.DareAndTruth.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Set<ChallengeSummaryProjection> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<ChallengeSummaryProjection> findByIdAndIsDeletedFalse(UUID uuid);

    String SPECIAL_CHARACTERS = "áàạảãâấầậẩẫăắằặẳẵéèẹẻẽêếềệểễíìịỉĩóòọỏõôốồộổỗơớờợởỡúùụủũưứừựửữýỳỵỷỹđ";
    String REPLACEMENT_CHARACTERS = "aaaaaaaaaaaaaaaaaeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuyyyyyd";

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
		SELECT new PNV.DareAndTruth.dto.response.challenge.ChallengeWithUserAndLikeCountAndCommentCountResponse(
			c.id, c.hashtag, c.content, c.mediaUrl,
			c.startDate, c.endDate, c.createdAt,
			c.user.id, c.user.username,
			COUNT(DISTINCT l.id),
			COUNT(DISTINCT cm.id),
			CASE WHEN COUNT(DISTINCT likedByUser.id) > 0 THEN true ELSE false END
		)
		FROM Challenge c
		LEFT JOIN Like l ON c.id = l.feedId
		LEFT JOIN Comment cm ON c.id = cm.feedId
		LEFT JOIN Like likedByUser ON c.id = likedByUser.feedId AND likedByUser.user.id = :userId
		WHERE c.isDeleted = false AND c.isActive = true
		GROUP BY c.id, c.hashtag, c.content, c.mediaUrl, c.startDate, c.endDate, c.createdAt, c.user.id, c.user.username
		ORDER BY c.updatedAt DESC
	""")
    List<ChallengeWithUserAndLikeCountAndCommentCountResponse> findAllChallengesWithLikeCountAndCommentCount(
            @Param("userId") UUID userId);

    @Query("SELECT c FROM Challenge c WHERE " + "LOWER(TRANSLATE(c.hashtag, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "')) "
            + "ILIKE LOWER(CONCAT('%', :word, '%')) "
            + "OR LOWER(TRANSLATE(c.content, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "')) "
            + "ILIKE LOWER(CONCAT('%', :word, '%'))")
    List<ChallengeSummaryProjection> searchChallengesBySingleWord(@Param("word") String word);

    @Query("SELECT c FROM Challenge c WHERE " + "LOWER(REPLACE(TRANSLATE(c.hashtag, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "'), ' ', '')) "
            + "ILIKE LOWER(CONCAT('%', :normalizedKeyword, '%')) "
            + "OR LOWER(REPLACE(TRANSLATE(c.content, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "'), ' ', '')) "
            + "ILIKE LOWER(CONCAT('%', :normalizedKeyword, '%'))"
            + "AND c.id NOT IN :excludedIds")
    List<ChallengeSummaryProjection> searchChallengesByNormalizedKeyword(
            @Param("normalizedKeyword") String normalizedKeyword, @Param("excludedIds") List<UUID> excludedIds);

    @Query("SELECT c FROM Challenge c WHERE " + "(LOWER(TRANSLATE(c.hashtag, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "')) "
            + "ILIKE LOWER(CONCAT('%', :normalizedKeyword, '%')) "
            + "OR LOWER(TRANSLATE(c.content, '"
            + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "')) "
            + "ILIKE LOWER(CONCAT('%', :normalizedKeyword, '%'))) "
            + "AND c.id NOT IN :excludedIds")
    List<ChallengeSummaryProjection> searchChallengesExcludingIds(
            @Param("normalizedKeyword") String normalizedKeyword, @Param("excludedIds") List<UUID> excludedIds);
}

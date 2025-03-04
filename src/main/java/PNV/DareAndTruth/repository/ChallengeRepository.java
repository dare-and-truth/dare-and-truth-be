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
import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
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

    @Query("SELECT new PNV.DareAndTruth.dto.response.feed.GetFeedResponse("
            + "c.id, 'challenge', c.hashtag, c.content, c.mediaUrl, "
            + "CAST(c.startDate AS string), CAST(c.endDate AS string), c.createdAt, "
            + "c.user.id, c.user.username, "
            + "COALESCE(COUNT(DISTINCT l.id), 0), COALESCE(COUNT(DISTINCT cm.id), 0), "
            + "CASE WHEN COUNT(DISTINCT likedByUser.id) > 0 THEN true ELSE false END, "
            + "CASE WHEN COUNT(DISTINCT r.user.id) > 0 THEN true ELSE false END) "
            + "FROM Challenge c "
            + "LEFT JOIN Like l ON c.id = l.feedId "
            + "LEFT JOIN Comment cm ON c.id = cm.feedId "
            + "LEFT JOIN Like likedByUser ON c.id = likedByUser.feedId AND likedByUser.user.id = :userId "
            + "LEFT JOIN Reminder r ON c.hashtag = r.hashtag "
            + "AND c.startDate = r.startDate "
            + "AND c.endDate = r.endDate "
            + "AND r.user.id = :userId "
            + "WHERE c.isDeleted = false AND c.isActive = true "
            + "AND (LOWER(REPLACE(TRANSLATE(c.hashtag, :specialChars, :replaceChars), ' ', '')) "
            + "LIKE LOWER(CONCAT('%', :normalizedKeyword, '%')) "
            + "OR LOWER(REPLACE(TRANSLATE(c.content, :specialChars, :replaceChars), ' ', '')) "
            + "LIKE LOWER(CONCAT('%', :normalizedKeyword, '%'))) "
            + "GROUP BY c.id, c.hashtag, c.content, c.mediaUrl, c.startDate, c.endDate, c.createdAt, c.user.id, c.user.username "
            + "ORDER BY c.createdAt DESC")
    List<GetFeedResponse> searchChallengesByNormalizedKeyword(
            @Param("normalizedKeyword") String normalizedKeyword,
            @Param("userId") UUID userId,
            @Param("specialChars") String specialChars,
            @Param("replaceChars") String replaceChars);

    @Query("SELECT new PNV.DareAndTruth.dto.response.feed.GetFeedResponse("
            + "c.id, 'challenge', c.hashtag, c.content, c.mediaUrl, "
            + "CAST(c.startDate AS string), CAST(c.endDate AS string), c.createdAt, "
            + "c.user.id, c.user.username, "
            + "COALESCE(COUNT(DISTINCT l.id), 0), COALESCE(COUNT(DISTINCT cm.id), 0), "
            + "CASE WHEN COUNT(DISTINCT likedByUser.id) > 0 THEN true ELSE false END, "
            + "CASE WHEN COUNT(DISTINCT r.user.id) > 0 THEN true ELSE false END) "
            + "FROM Challenge c "
            + "LEFT JOIN Like l ON c.id = l.feedId "
            + "LEFT JOIN Comment cm ON c.id = cm.feedId "
            + "LEFT JOIN Like likedByUser ON c.id = likedByUser.feedId AND likedByUser.user.id = :userId "
            + "LEFT JOIN Reminder r ON c.hashtag = r.hashtag "
            + "AND c.startDate = r.startDate "
            + "AND c.endDate = r.endDate "
            + "AND r.user.id = :userId "
            + "WHERE c.isDeleted = false AND c.isActive = true "
            + "AND c.id NOT IN :excludedIds "
            + "AND (LOWER(TRANSLATE(c.hashtag, :specialChars, :replaceChars)) "
            + "LIKE LOWER(CONCAT('%', :word, '%')) "
            + "OR LOWER(TRANSLATE(c.content, :specialChars, :replaceChars)) "
            + "LIKE LOWER(CONCAT('%', :word, '%'))) "
            + "GROUP BY c.id, c.hashtag, c.content, c.mediaUrl, c.startDate, c.endDate, c.createdAt, c.user.id, c.user.username "
            + "ORDER BY c.createdAt DESC")
    List<GetFeedResponse> searchChallengesBySingleWord(
            @Param("word") String word,
            @Param("excludedIds") List<UUID> excludedIds,
            @Param("specialChars") String specialChars,
            @Param("replaceChars") String replaceChars,
            @Param("userId") UUID userId);

    @Query("SELECT new PNV.DareAndTruth.dto.response.feed.GetFeedResponse("
            + "c.id, 'challenge', c.hashtag, c.content, c.mediaUrl, "
            + "CAST(c.startDate AS string), CAST(c.endDate AS string), c.createdAt, "
            + "c.user.id, c.user.username, "
            + "COALESCE(COUNT(DISTINCT l.id), 0), COALESCE(COUNT(DISTINCT cm.id), 0), "
            + "CASE WHEN COUNT(DISTINCT likedByUser.id) > 0 THEN true ELSE false END, "
            + "CASE WHEN COUNT(DISTINCT r.user.id) > 0 THEN true ELSE false END) "
            + "FROM Challenge c "
            + "LEFT JOIN Like l ON c.id = l.feedId "
            + "LEFT JOIN Comment cm ON c.id = cm.feedId "
            + "LEFT JOIN Like likedByUser ON c.id = likedByUser.feedId AND likedByUser.user.id = :userId "
            + "LEFT JOIN Reminder r ON c.hashtag = r.hashtag "
            + "AND c.startDate = r.startDate "
            + "AND c.endDate = r.endDate "
            + "AND r.user.id = :userId "
            + "WHERE c.isDeleted = false AND c.isActive = true "
            + "AND c.id NOT IN :excludedIds "
            + "AND (LOWER(REPLACE(TRANSLATE(c.hashtag, :specialChars, :replaceChars), ' ', '')) "
            + "LIKE LOWER(CONCAT('%', :normalizedKeyword, '%')) "
            + "OR LOWER(REPLACE(TRANSLATE(c.content, :specialChars, :replaceChars), ' ', '')) "
            + "LIKE LOWER(CONCAT('%', :normalizedKeyword, '%'))) "
            + "GROUP BY c.id, c.hashtag, c.content, c.mediaUrl, c.startDate, c.endDate, c.createdAt, c.user.id, c.user.username "
            + "ORDER BY c.createdAt DESC")
    List<GetFeedResponse> searchChallengesExcludingIds(
            @Param("normalizedKeyword") String normalizedKeyword,
            @Param("excludedIds") List<UUID> excludedIds,
            @Param("specialChars") String specialChars,
            @Param("replaceChars") String replaceChars,
            @Param("userId") UUID userId);
}

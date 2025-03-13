package PNV.DareAndTruth.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import PNV.DareAndTruth.dto.response.feed.GetFeedResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PNV.DareAndTruth.dto.response.feed.FeedResponse;
import PNV.DareAndTruth.entity.Post;

@Repository
public interface FeedRepository extends JpaRepository<Post, UUID> {
    @Query(
            value =
                    """
					SELECT f.id, f.type, f.hashtag, f.content, f.media_url,
						f.start_date, f.end_date, f.created_at,
						f.user_id, u.username, u.avatar_url,
						COALESCE(l.like_count, 0) AS like_count,
						COALESCE(c.comment_count, 0) AS comment_count,
						CASE WHEN ul.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_like,
						CASE WHEN f.type = 'challenge' AND r.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_joined
					FROM (
						SELECT p.id, 'post' AS type, p.hashtag, p.content, p.media_url,
							NULL AS start_date, NULL AS end_date, p.user_id, p.created_at
						FROM posts p WHERE p.is_deleted = FALSE
						UNION ALL
						SELECT c.id, 'challenge' AS type, c.hashtag, c.content, c.media_url,
							c.start_date, c.end_date, c.user_id, c.created_at
						FROM challenges c WHERE c.is_deleted = FALSE
					) AS f
					JOIN users u ON f.user_id = u.id
					LEFT JOIN (
						SELECT feed_id, COUNT(*) AS like_count
						FROM likes
						GROUP BY feed_id
					) AS l ON f.id = l.feed_id
					LEFT JOIN (
						SELECT feed_id, COUNT(*) AS comment_count
						FROM comments
						GROUP BY feed_id
					) AS c ON f.id = c.feed_id
					LEFT JOIN likes ul ON f.id = ul.feed_id AND ul.user_id = :currentUserId
					LEFT JOIN reminders r ON f.type = 'challenge'
						AND f.hashtag = r.hashtag
						AND f.start_date = r.start_date
						AND f.end_date = r.end_date
						AND r.user_id = :currentUserId
					ORDER BY f.id DESC
					LIMIT :limit OFFSET :offset
					""",
            nativeQuery = true)
    List<Object[]> getFeedWithCounts(
            @Param("currentUserId") UUID currentUserId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(
            value =
                    """
					SELECT f.id, f.type, f.hashtag, f.content, f.media_url,
						f.start_date, f.end_date, f.created_at,
						f.user_id, u.username, u.avatar_url,
						COALESCE(l.like_count, 0) AS like_count,
						COALESCE(c.comment_count, 0) AS comment_count,
						CASE WHEN ul.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_like,
						CASE WHEN f.type = 'challenge' AND r.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_joined
					FROM (
						SELECT p.id, 'post' AS type, p.hashtag, p.content, p.media_url,
							NULL AS start_date, NULL AS end_date, p.user_id, p.created_at
						FROM posts p WHERE p.is_deleted = FALSE
						UNION ALL
						SELECT c.id, 'challenge' AS type, c.hashtag, c.content, c.media_url,
							c.start_date, c.end_date, c.user_id, c.created_at
						FROM challenges c WHERE c.is_deleted = FALSE
					) AS f
					JOIN users u ON f.user_id = u.id
					LEFT JOIN (
						SELECT feed_id, COUNT(*) AS like_count
						FROM likes
						GROUP BY feed_id
					) AS l ON f.id = l.feed_id
					LEFT JOIN (
						SELECT feed_id, COUNT(*) AS comment_count
						FROM comments
						GROUP BY feed_id
					) AS c ON f.id = c.feed_id
					LEFT JOIN likes ul ON f.id = ul.feed_id AND ul.user_id = :currentUserId
					LEFT JOIN reminders r ON f.type = 'challenge'
						AND f.hashtag = r.hashtag
						AND f.start_date = r.start_date
						AND f.end_date = r.end_date
						AND r.user_id = :currentUserId
					WHERE f.type = :type
					AND f.user_id = :currentUserId
					ORDER BY f.id DESC
					LIMIT :limit OFFSET :offset
					""",
            nativeQuery = true)
    List<Object[]> getFeedWithCountsAndType(
            @Param("currentUserId") UUID currentUserId,
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("type") String type);

    @Query(
            value =
		"""
			SELECT f.id, f.type, f.hashtag, f.content, f.media_url,
				f.start_date, f.end_date, f.created_at,
				f.user_id, u.username, u.avatar_url,
				COALESCE(l.like_count, 0) AS like_count,
				COALESCE(c.comment_count, 0) AS comment_count,
				CASE WHEN ul.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS liked,
				CASE WHEN f.type = 'challenge' AND r.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS joined
			FROM (
				SELECT p.id, 'post' AS type, p.hashtag, p.content, p.media_url,
				NULL AS start_date, NULL AS end_date, p.user_id, p.created_at
				FROM posts p WHERE p.is_deleted = FALSE AND p.id = :id AND 'post' = :type
				UNION ALL
				SELECT c.id, 'challenge' AS type, c.hashtag, c.content, c.media_url,
				c.start_date, c.end_date, c.user_id, c.created_at
				FROM challenges c WHERE c.is_deleted = FALSE AND c.id = :id AND 'challenge' = :type
			) AS f
			JOIN users u ON f.user_id = u.id
			LEFT JOIN (
				SELECT feed_id, COUNT(*) AS like_count
				FROM likes
				GROUP BY feed_id
			) AS l ON f.id = l.feed_id
			LEFT JOIN (
				SELECT feed_id, COUNT(*) AS comment_count
				FROM comments
				GROUP BY feed_id
			) AS c ON f.id = c.feed_id
			LEFT JOIN likes ul ON f.id = ul.feed_id AND ul.user_id = :currentUserId
			LEFT JOIN reminders r ON f.type = 'challenge'
				AND f.hashtag = r.hashtag
				AND f.start_date = r.start_date
				AND f.end_date = r.end_date
				AND r.user_id = :currentUserId
		""",
            nativeQuery = true)
    FeedResponse findFeedByIdAndType(
            @Param("id") UUID id, @Param("type") String type, @Param("currentUserId") UUID currentUserId);

	@Query(
			value =
					"""
                    SELECT DISTINCT f.id, f.type, f.hashtag, f.content, f.media_url,
                        f.start_date, f.end_date, f.created_at,
                        f.user_id, u.username, u.avatar_url,
                        COALESCE(l_count.like_count, 0) AS like_count,
                        COALESCE(c.comment_count, 0) AS comment_count,
                        TRUE AS is_like, 
                        CASE WHEN f.type = 'challenge' AND r.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_joined,
                        l.liked_at
                    FROM (
                        SELECT p.id, 'post' AS type, p.hashtag, p.content, p.media_url,
                            NULL AS start_date, NULL AS end_date, p.user_id, p.created_at
                        FROM posts p WHERE p.is_deleted = FALSE
                        UNION ALL
                        SELECT c.id, 'challenge' AS type, c.hashtag, c.content, c.media_url,
                            c.start_date, c.end_date, c.user_id, c.created_at
                        FROM challenges c WHERE c.is_deleted = FALSE
                    ) AS f
                    JOIN likes l ON f.id = l.feed_id
                    JOIN users u ON f.user_id = u.id
                    LEFT JOIN (
                        SELECT feed_id, COUNT(*) AS like_count
                        FROM likes
                        GROUP BY feed_id
                    ) AS l_count ON f.id = l_count.feed_id
                    LEFT JOIN (
                        SELECT feed_id, COUNT(*) AS comment_count
                        FROM comments
                        GROUP BY feed_id
                    ) AS c ON f.id = c.feed_id
                    LEFT JOIN reminders r ON f.type = 'challenge'
                        AND f.hashtag = r.hashtag
                        AND f.start_date = r.start_date
                        AND f.end_date = r.end_date
                        AND r.user_id = :userId
                    WHERE l.user_id = :userId
                    ORDER BY l.liked_at DESC
                    LIMIT :limit OFFSET :offset;
                    """,
			nativeQuery = true)
	List<Object[]> getFeedsLovedByUser(
			@Param("userId") UUID userId,
			@Param("limit") int limit,
			@Param("offset") int offset);

	@Query(
			value = """
    SELECT DISTINCT f.id, 'challenge' AS type, f.hashtag, f.content, f.media_url AS mediaUrl,
           f.start_date AS startDate, f.end_date AS endDate, f.created_at AS createdAt, 
           u.id, u.username, u.avatar_url,
           COALESCE(l.like_count, 0) AS likeCount,
           COALESCE(c.comment_count, 0) AS commentCount,
           COUNT(*) OVER() AS totalPosts,
           CASE WHEN ul.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_like,
           CASE WHEN r.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_joined
    FROM challenges f
    JOIN users u ON f.user_id = u.id
    LEFT JOIN (
        SELECT feed_id, COUNT(*) AS like_count FROM likes GROUP BY feed_id
    ) AS l ON f.id = l.feed_id
    LEFT JOIN (
        SELECT feed_id, COUNT(*) AS comment_count FROM comments GROUP BY feed_id
    ) AS c ON f.id = c.feed_id
    LEFT JOIN likes ul ON f.id = ul.feed_id AND ul.user_id = :userId
    LEFT JOIN reminders r ON f.hashtag = r.hashtag
        AND f.start_date = r.start_date
        AND f.end_date = r.end_date
        AND r.user_id = :userId
    WHERE f.is_deleted = FALSE
          AND f.hashtag = :hashtag
          AND f.start_date = :startDate
          AND f.end_date = :endDate
    ORDER BY f.created_at DESC
    LIMIT :size OFFSET :offset
    """,
			nativeQuery = true
	)
	List<Object[]> getChallengeFeed(
			@Param("hashtag") String hashtag,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,
			@Param("userId") UUID userId,
			@Param("size") int size,
			@Param("offset") int offset
	);


	@Query(
			value = """
    SELECT DISTINCT f.id, 'post' AS type, f.hashtag, f.content, f.media_url AS mediaUrl,
           NULL AS startDate, NULL AS endDate, f.created_at AS createdAt,
           u.id, u.username, u.avatar_url,
           COALESCE(l.like_count, 0) AS likeCount,
           COALESCE(c.comment_count, 0) AS commentCount,
           COUNT(*) OVER() AS totalPosts,
           CASE WHEN ul.user_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_like,
           FALSE AS is_joined
    FROM posts f
    JOIN users u ON f.user_id = u.id
    LEFT JOIN (
        SELECT feed_id, COUNT(*) AS like_count FROM likes GROUP BY feed_id
    ) AS l ON f.id = l.feed_id
    LEFT JOIN (
        SELECT feed_id, COUNT(*) AS comment_count FROM comments GROUP BY feed_id
    ) AS c ON f.id = c.feed_id
    LEFT JOIN likes ul ON f.id = ul.feed_id AND ul.user_id = :userId
    WHERE f.is_deleted = FALSE
          AND f.hashtag = :hashtag
          AND f.created_at BETWEEN :startDateTime AND :endDateTime
    ORDER BY f.created_at DESC
    LIMIT :size OFFSET :offset
    """,
			nativeQuery = true
	)
	List<Object[]> getPostFeed(
			@Param("hashtag") String hashtag,
			@Param("startDateTime") Timestamp startDateTime,
			@Param("endDateTime") Timestamp endDateTime,
			@Param("userId") UUID userId,
			@Param("size") int size,
			@Param("offset") int offset
	);
}

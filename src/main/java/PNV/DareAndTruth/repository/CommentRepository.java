package PNV.DareAndTruth.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Set<CommentSummaryProjection> findAllByFeedIdAndParentCommentIsNullOrderByCreatedAtDesc(UUID feedId);

    List<CommentSummaryProjection> findAllByParentComment_IdOrderByCreatedAtAsc(UUID parentCommentId);

    @Query(
            "SELECT c.parentComment.id, COUNT(c) FROM Comment c WHERE c.parentComment.id IN :commentIds GROUP BY c.parentComment.id")
    List<Object[]> countRepliesForComments(@Param("commentIds") Set<UUID> commentIds);

    Optional<CommentSummaryProjection> findCommentById(UUID commentId);

    @Query(
            """
	SELECT c FROM Comment c
	WHERE c.feedId = :feedId AND c.parentComment IS NULL
	ORDER BY CASE WHEN c.user.id = :feedUserId THEN 0 ELSE 1 END, c.createdAt DESC
	""")
    List<CommentSummaryProjection> findAllByFeedIdAndParentCommentIsNullOrderByFeedUserFirst(
            @Param("feedId") UUID feedId, @Param("feedUserId") UUID feedUserId);
}

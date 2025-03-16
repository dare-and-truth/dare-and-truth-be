package PNV.DareAndTruth.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Set<CommentSummaryProjection> findAllByFeedIdAndParentCommentIsNullOrderByCreatedAtDesc(UUID feedId);
    Set<CommentSummaryProjection> findAllByParentCommentIdOrderByCreatedAtAsc(UUID parentCommentId);
}

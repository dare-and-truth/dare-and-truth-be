package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Set<CommentSummaryProjection> findAllByFeedIdOrderByCreatedAtDesc(UUID feedId);
}

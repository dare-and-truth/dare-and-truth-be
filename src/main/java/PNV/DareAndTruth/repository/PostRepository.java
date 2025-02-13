package PNV.DareAndTruth.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.dto.projection.post.PostSummaryProjection;
import PNV.DareAndTruth.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Set<PostSummaryProjection> findAllByIsDeletedFalse();
}

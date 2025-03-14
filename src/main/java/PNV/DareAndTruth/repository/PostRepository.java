package PNV.DareAndTruth.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import PNV.DareAndTruth.dto.projection.post.PostSummaryProjection;
import PNV.DareAndTruth.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Set<PostSummaryProjection> findAllByIsDeletedFalse();


    Optional<PostSummaryProjection> findByIdAndIsDeletedFalse(UUID id);

    // At the time Nhat or anyone fix type when save like, I will add this in to query "AND l.feedType = 'post', in
    // second line "
    @Query("SELECT p.user.id, p.user.username, p.user.avatarUrl, COUNT(l.id) " + "FROM Post p "
            + "LEFT JOIN Like l ON l.feedId = p.id AND l.feedType = 'post' "
            + "WHERE p.hashtag = :hashtag "
            + "AND CAST(p.createdAt AS DATE) BETWEEN :startDate AND :endDate "
            + "AND (CAST(l.likedAt AS DATE) BETWEEN :startDate AND :endDate OR l.likedAt IS NULL) "
            + "GROUP BY p.user.id, p.user.username, p.user.avatarUrl "
            + "ORDER BY COUNT(l.id) DESC")
    List<Object[]> getPostByHashtagAndDate(
            @Param("hashtag") String hashtag,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<Post> findPostEntityByIdAndIsDeletedFalse(UUID id);
}

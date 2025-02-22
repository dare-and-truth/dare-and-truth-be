package PNV.DareAndTruth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Like;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndFeedId(UUID userId, UUID feedId);

    Optional<Like> findByUserIdAndFeedId(UUID userId, UUID feedId);

    void deleteByFeedId(UUID feedId);
}

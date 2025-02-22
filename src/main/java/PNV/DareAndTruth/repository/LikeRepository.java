package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndFeedId(UUID userId, UUID feedId);

    Optional<Like> findByUserIdAndFeedId(UUID userId, UUID feedId);
}

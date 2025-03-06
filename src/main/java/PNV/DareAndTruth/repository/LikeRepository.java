package PNV.DareAndTruth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndFeedId(UUID userId, UUID feedId);

    Optional<Like> findByUserIdAndFeedId(UUID userId, UUID feedId);

}

package PNV.DareAndTruth.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Set<ChallengeSummaryProjection> findAllByIsDeletedFalse();

    Optional<ChallengeSummaryProjection> findByIdAndIsDeletedFalse(UUID uuid);
}

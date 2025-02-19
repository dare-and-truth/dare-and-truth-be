package PNV.DareAndTruth.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.dto.projection.challenge.ChallengeSummaryProjection;
import PNV.DareAndTruth.entity.Challenge;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    Set<ChallengeSummaryProjection> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<ChallengeSummaryProjection> findByIdAndIsDeletedFalse(UUID uuid);

    @Query("""
        SELECT COUNT(c) > 0 FROM Challenge c WHERE c.hashtag = :hashtag
        AND (c.startDate BETWEEN :startDate AND :endDate
             OR c.endDate BETWEEN :startDate AND :endDate)
    """)
    boolean existsWithOverlappingDates(
            @Param("hashtag") String hashtag,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

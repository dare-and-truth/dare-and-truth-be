package PNV.DareAndTruth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    boolean existsByTitle(@NotBlank(message = "BADGE_TITLE_REQUIRED") String title);

    List<Badge> findAllByIsDeletedFalse();

    Optional<Badge> findByIdAndIsDeletedFalse(UUID id);
}

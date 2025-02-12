package PNV.DareAndTruth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Badge;

public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    List<Badge> findAllByIsDeletedFalse();

    Optional<Badge> findByIdAndIsDeletedFalse(UUID id);
}

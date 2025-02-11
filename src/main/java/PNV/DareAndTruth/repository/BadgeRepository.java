package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    List<Badge> findAllByIsDeletedFalse();
    Optional<Badge> findByIdAndIsDeletedFalse(UUID id);
    boolean existsByTitle(String title);
}

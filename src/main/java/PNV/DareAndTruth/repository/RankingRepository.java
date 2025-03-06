package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RankingRepository extends JpaRepository<Challenge, UUID> {
}

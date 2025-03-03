package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {
  }
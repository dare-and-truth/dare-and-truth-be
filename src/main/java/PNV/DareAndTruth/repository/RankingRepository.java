package PNV.DareAndTruth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Challenge;

public interface RankingRepository extends JpaRepository<Challenge, UUID> {}

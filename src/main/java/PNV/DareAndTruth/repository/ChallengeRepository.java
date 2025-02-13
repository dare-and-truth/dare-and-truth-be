package PNV.DareAndTruth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {}

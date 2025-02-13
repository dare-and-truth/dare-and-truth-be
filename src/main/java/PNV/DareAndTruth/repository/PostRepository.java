package PNV.DareAndTruth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import PNV.DareAndTruth.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {}

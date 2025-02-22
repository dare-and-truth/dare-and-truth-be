package PNV.DareAndTruth.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PNV.DareAndTruth.dto.projection.user.UserDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserSummaryProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<UserWithIdProjection> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    Set<UserSummaryProjection> findAllByIsDeletedFalse();

    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.isDeleted = false")
    Optional<UserDetailProjection> findDetailById(@Param("userId") UUID userId);
}

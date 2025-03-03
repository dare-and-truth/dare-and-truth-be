package PNV.DareAndTruth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.entity.Request;
import PNV.DareAndTruth.entity.User;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    Optional<Request> findByUserAndFollower(User user, User follower);

    @Query(
            """
			SELECT r
			FROM Request r
			WHERE (r.user.id = :existingUserId OR r.follower.id = :existingUserId1)
			AND r.isAccepted = true
			""")
    List<FriendDetailProjection> findAllByUserIdOrFollowerIdAndIsAcceptedTrue(
            UUID existingUserId, UUID existingUserId1);

    @Query(
            """
			SELECT r
			FROM Request r
			WHERE (r.user.id = :existingUserId OR r.follower.id = :existingUserId1)
			AND r.isAccepted = false
			""")
    List<FriendDetailProjection> findAllByUserIdOrFollowerIdAndIsAcceptedFalse(
            UUID existingUserId, UUID existingUserId1);
}

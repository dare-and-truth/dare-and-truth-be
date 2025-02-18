package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Request;
import PNV.DareAndTruth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
    List<Request> findAllByUser(User user);
    Optional<Request> findByUserAndFollower(User user, User follower);

    List<Request> findAllByUserIdOrFollowerId(UUID uuid, UUID uuid1);
}

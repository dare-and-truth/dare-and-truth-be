package PNV.DareAndTruth.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PNV.DareAndTruth.dto.projection.request.FriendDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserDetailProjection;
import PNV.DareAndTruth.dto.projection.user.UserSummaryProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdAndUsernameAndAvatarProjection;
import PNV.DareAndTruth.dto.projection.user.UserWithIdProjection;
import PNV.DareAndTruth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    String SPECIAL_CHARACTERS = "áàạảãâấầậẩẫăắằặẳẵéèẹẻẽêếềệểễíìịỉĩóòọỏõôốồộổỗơớờợởỡúùụủũưứừựửữýỳỵỷỹđ";
    String REPLACEMENT_CHARACTERS = "aaaaaaaaaaaaaaaaaeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuyyyyyd";

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalseAndIsActiveTrue(String email);

    Optional<UserWithIdProjection> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    Set<UserSummaryProjection> findAllByIsDeletedFalse();

    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.isDeleted = false")
    Optional<UserDetailProjection> findDetailById(@Param("userId") UUID userId);

    boolean existsByIdAndIsDeletedFalse(UUID uuid);

    @Query("SELECT u FROM User u WHERE LOWER(REPLACE(TRANSLATE(u.username, '" + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "'), ' ', '')) ILIKE LOWER(CONCAT('%', :normalizedKeyword, '%')) AND u.id <> :currentUserId")
    List<UserWithIdAndUsernameAndAvatarProjection> searchUsersByNormalizedKeyword(
            @Param("normalizedKeyword") String normalizedKeyword, @Param("currentUserId") UUID currentUserId);

    @Query("SELECT u FROM User u WHERE LOWER(TRANSLATE(u.username, '" + SPECIAL_CHARACTERS
            + "', '"
            + REPLACEMENT_CHARACTERS
            + "')) ILIKE LOWER(CONCAT('%', :word, '%')) AND u.id <> :currentUserId AND u.id NOT IN :excludedIds")
    List<UserWithIdAndUsernameAndAvatarProjection> searchUsersBySingleWord(
            @Param("word") String word,
            @Param("currentUserId") UUID currentUserId,
            @Param("excludedIds") List<UUID> excludedIds);

    @Query(
            """
			SELECT r
			FROM Request r
			WHERE (r.user.id = :userId OR r.follower.id = :userId)
			ORDER BY r.followedAt DESC
			""")
    List<FriendDetailProjection> findRequestsByUserId(@Param("userId") UUID userId);

    Optional<UserWithIdAndUsernameAndAvatarProjection> findBasicDetailById(UUID userId);

    @Query("SELECT u.id, u.username, u.avatarUrl, COALESCE(SUM(s.scoreReceived), 0) "
            + "FROM User u LEFT JOIN Score s ON u.id = s.user.id "
            + "GROUP BY u.id, u.username, u.avatarUrl "
            + "ORDER BY SUM(s.scoreReceived) DESC")
    List<Object[]> getAllUsersWithScores();
}

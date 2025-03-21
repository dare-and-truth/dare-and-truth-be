package PNV.DareAndTruth.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
import PNV.DareAndTruth.dto.response.hashtag.HashtagForDoChallengeResponse;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Reminder;
import PNV.DareAndTruth.entity.User;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    Optional<Reminder> findByUserAndTitleAndHashtagAndStartDateAndEndDateAndStartTimeAndEndTime(
            User user,
            String title,
            String hashtag,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime);

    @Query("SELECT r.id AS id, r.title AS title, r.hashtag AS hashtag, r.startDate AS startDate, r.endDate AS endDate, "
            + "r.reminderContent AS reminderContent, r.reminderTime AS reminderTime, r.startTime AS startTime, r.endTime AS endTime, "
            + "r.user.id AS userId "
            + "FROM Reminder r WHERE r.user = :user AND :date BETWEEN r.startDate AND r.endDate")
    List<ReminderSummaryProjection> findByUserAndDate(User user, LocalDate date);

    @Query(
            "SELECT new PNV.DareAndTruth.dto.response.hashtag.HashtagForDoChallengeResponse(r.hashtag, CASE WHEN COUNT(p.id) > 0 THEN true ELSE false END) "
                    + "FROM Reminder r "
                    + "LEFT JOIN Post p ON r.hashtag = p.hashtag "
                    + "AND p.createdAt >= :startOfDay "
                    + "AND p.isActive = true "
                    + "AND p.isDeleted = false "
                    + "WHERE r.user.id = :userId "
                    + "AND :today BETWEEN r.startDate AND r.endDate "
                    + "AND r.hashtag IS NOT NULL "
                    + "GROUP BY r.hashtag")
    List<HashtagForDoChallengeResponse> findHashtagsForUserToday(
            @Param("userId") UUID userId,
            @Param("today") LocalDate today,
            @Param("startOfDay") LocalDateTime startOfDay);

    @Query(
            """
		SELECT c FROM Challenge c
		JOIN Reminder r ON c.hashtag = r.hashtag
		WHERE c.startDate = r.startDate
		AND c.endDate = r.endDate
		AND c.endDate <= CURRENT_DATE
		""")
    List<Challenge> findEndedChallengesInReminder();

    @Query("SELECT COUNT(DISTINCT r.user.id) FROM Reminder r " + "WHERE r.hashtag = :hashtag "
            + "AND r.startDate = :startDate "
            + "AND r.endDate = :endDate")
    int countParticipantsByHashtagAndDateRange(
            @Param("hashtag") String hashtag,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Reminder> findByEndDateGreaterThanEqual(LocalDate endDate);

    @Query("SELECT DISTINCT r.user.id FROM Reminder r WHERE r.hashtag = :hashtag AND r.startDate = :startDate AND r.endDate = :endDate")
    List<UUID> findUserIdsByHashtagAndDateRange(@Param("hashtag") String hashtag, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

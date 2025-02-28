package PNV.DareAndTruth.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import PNV.DareAndTruth.dto.projection.reminder.ReminderSummaryProjection;
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

    @Query("SELECT c.id AS id, c.title AS title, c.hashtag AS hashtag, c.startDate AS startDate, c.endDate AS endDate, "
            + "c.reminderContent AS reminderContent, c.reminderTime AS reminderTime, c.startTime AS startTime, c.endTime AS endTime, "
            + "c.user.id AS userId "
            + "FROM Reminder c WHERE c.user = :user AND :date BETWEEN c.startDate AND c.endDate")
    List<ReminderSummaryProjection> findByUserAndDate(User user, LocalDate date);
}

package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.dto.projection.calendar.CalendarSummaryProjection;
import PNV.DareAndTruth.entity.Calendar;
import PNV.DareAndTruth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalendarRepository extends JpaRepository<Calendar, UUID> {
    Optional<Calendar> findByUserAndTitleAndStartDateAndEndDateAndStartTimeAndEndTimeAndRepeatTypeAndIsChallenge(
            User user, String title, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime, int repeatType, Boolean isChallenge);

    @Query("SELECT c.id AS id, c.title AS title, c.startDate AS startDate, c.endDate AS endDate, " +
            "c.startTime AS startTime, c.endTime AS endTime, c.repeatType AS repeatType, " +
            "c.isChallenge AS isChallenge, c.user.id AS userId " +
            "FROM Calendar c WHERE c.user = :user AND :date BETWEEN c.startDate AND c.endDate")
    List<CalendarSummaryProjection> findByUserAndDate(User user, LocalDate date);
}

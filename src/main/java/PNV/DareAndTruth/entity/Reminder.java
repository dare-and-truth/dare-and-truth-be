package PNV.DareAndTruth.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reminders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reminder extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "title")
    String title;

    @Column(name = "hashtag")
    String hashtag;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "reminder_content", nullable = false,columnDefinition = "TEXT")
    String reminderContent;

    @Column(name = "reminder_time")
    LocalTime reminderTime;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reminder that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(user, that.user)
                && Objects.equals(title, that.title)
                && Objects.equals(hashtag, that.hashtag)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(reminderTime, that.reminderTime)
                && Objects.equals(reminderContent, that.reminderContent)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                user,
                title,
                hashtag,
                startDate,
                endDate,
                reminderTime,
                reminderContent,
                startTime,
                endTime);
    }
}

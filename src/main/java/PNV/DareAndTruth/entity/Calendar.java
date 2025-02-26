package PNV.DareAndTruth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calendars")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Calendar extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalTime endTime;

    @Column(name = "repeat_type", nullable = false)
    int repeatType; //1-Does not repeat , 2-Daily, 3-weekly, 4-monthly

    @Column(name = "is_challenge", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isChallenge = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calendar that)) return false;
        if (!super.equals(o)) return false;
        return repeatType == that.repeatType &&
                user.equals(that.user) &&
                title.equals(that.title) &&
                startDate.equals(that.startDate) &&
                endDate.equals(that.endDate) &&
                startTime.equals(that.startTime) &&
                endTime.equals(that.endTime) &&
                isChallenge.equals(that.isChallenge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, title, startDate, endDate, startTime, endTime, repeatType, isChallenge);
    }

}

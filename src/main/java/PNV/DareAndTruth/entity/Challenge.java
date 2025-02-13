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
@Table(name = "Challenges")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Challenge extends BaseEntityAudit {
    @Column(name = "hashtag", nullable = false)
    String hashtag;

    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "media_url", nullable = false)
    String mediaUrl;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "is_required_on_time", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isRequiredOnTime = false;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(hashtag, challenge.hashtag)
                && Objects.equals(content, challenge.content)
                && Objects.equals(mediaUrl, challenge.mediaUrl)
                && Objects.equals(startDate, challenge.startDate)
                && Objects.equals(endDate, challenge.endDate)
                && Objects.equals(startTime, challenge.startTime)
                && Objects.equals(endTime, challenge.endTime)
                && Objects.equals(isRequiredOnTime, challenge.isRequiredOnTime)
                && Objects.equals(isActive, challenge.isActive)
                && Objects.equals(isDeleted, challenge.isDeleted)
                && Objects.equals(user, challenge.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                hashtag,
                content,
                mediaUrl,
                startDate,
                endDate,
                startTime,
                endTime,
                isRequiredOnTime,
                isActive,
                isDeleted,
                user);
    }
}

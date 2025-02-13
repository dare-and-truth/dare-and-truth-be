package PNV.DareAndTruth.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import org.hibernate.annotations.Check;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "badges")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Check(constraints = "start_day < end_day")
public class Badge extends BaseEntityAudit {

    @Column(name = "title", nullable = false, length = 100, unique = true)
    String title;

    @Column(name = "image", nullable = false)
    String image;

    @Column(name = "description", length = 500, nullable = false)
    String description;

    @Column(name = "badge_criteria", nullable = false)
    Integer badgeCriteria;

    @Column(name = "points", nullable = false)
    Integer points;

    @Column(name = "start_day", nullable = false)
    LocalDate startDay;

    @Column(name = "end_day", nullable = true)
    LocalDate endDay;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isDeleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Badge badge = (Badge) o;
        return this.getId() != null && this.getId().equals(badge.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}

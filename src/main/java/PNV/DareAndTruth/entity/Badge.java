package PNV.DareAndTruth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "badge")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Check(constraints = "start_day < end_day")
public class Badge extends BaseEntityAudit{

    @Column(name = "title", nullable = false, length = 100, unique = true)
    String title;

    @Column(name = "image")
    String image;

    @Column(name = "description", length = 500, nullable = false)
    String description;

    @Column(name = "required_count", nullable = false)
    int requiredCount;

    @Column(name = "points", nullable = false)
    int points;

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
}

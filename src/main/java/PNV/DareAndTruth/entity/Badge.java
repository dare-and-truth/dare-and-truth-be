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

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @Column(name = "required_count", nullable = false)
    private int requiredCount;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "start_day", nullable = false)
    private LocalDate startDay;

    @Column(name = "end_day", nullable = true)
    private LocalDate endDay;
}

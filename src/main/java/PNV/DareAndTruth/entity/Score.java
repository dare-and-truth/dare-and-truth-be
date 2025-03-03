package PNV.DareAndTruth.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scores")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Score extends BaseEntity{
    @Column(name = "type_of_score", nullable = false)
    String typeOfScore;

    @Column(name = "score_received", nullable = false)
    int scoreReceived;
}

package PNV.DareAndTruth.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scores")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Score extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    @Column(name = "score_received", nullable = false)
    int scoreReceived;

    /**
     * The type of score, indicating the source or reason for the points:
     * 1 = Ranking(1st:100,2nd:70,3rd:50,other:30,
     * 2 = Daily challenge completion(posted:10),
     * 3 = Like: 1 point for a like,
     * 4 = Number of participants in a challenge(>=1peo: 10, >=10peo: 30, >=100peo 50, >=10000peo: 100),
     * 5 = Comment: 2 point for first comment,
     * etc.
     */
    @Column(name = "score_type", nullable = false)
    int scoreType;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    @ToString.Exclude
    Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    Post post;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score score)) return false;
        if (!super.equals(o)) return false;
        return scoreReceived == score.scoreReceived
                && scoreType == score.scoreType
                && Objects.equals(user, score.user)
                && Objects.equals(createdAt, score.createdAt)
                && Objects.equals(challenge, score.challenge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, scoreReceived, scoreType, createdAt, challenge);
    }
}

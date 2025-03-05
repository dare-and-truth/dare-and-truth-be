package PNV.DareAndTruth.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a score record for a user, associated with activities like challenges, posts, or other actions.
 */
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

    /**
     * The user associated with this score.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    /**
     * The points received by the user for an action or activity.
     */
    @Column(name = "score_received", nullable = false)
    int scoreReceived;

    /**
     * The type of score, indicating the source or reason for the points:
     * 1 = Ranking(1st:100,2nd:70,3rd:50,other:30, 2 = Daily challenge completion(posted:10), 3 = Daily Login(5days:20,10days:50,25days:70,50days:100,), 4 = Number of participants in a challenge(>=1peo: 10, >=10peo: 30, >=100peo 50, >=10000peo: 100), etc.
     */
    @Column(name = "score_type", nullable = false)
    int scoreType;

    /**
     * The timestamp when the score was created.
     */
    @Column(name = "created_at", nullable = false)
    @CreatedDate
    LocalDateTime createdAt;

    /**
     * The challenge associated with this score, if applicable (e.g., for challenge completion).
     */
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    @ToString.Exclude
    Challenge challenge;

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

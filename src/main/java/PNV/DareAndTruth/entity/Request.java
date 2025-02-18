package PNV.DareAndTruth.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;

    @ManyToOne()
    @JoinColumn(name = "follower_id", nullable = false)
    @ToString.Exclude
    User follower; // Friend request sender

    @Column(name = "followed_at", nullable = false)
    LocalDateTime followedAt;

    @Column(name = "is_accepted", columnDefinition = "boolean default false")
    Boolean isAccepted;

    @Column(name = "accepted_at")
    LocalDateTime acceptedAt; // Thời điểm request được chấp nhận

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request request)) return false;
        return Objects.equals(getId(), request.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

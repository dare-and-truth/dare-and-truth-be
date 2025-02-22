package PNV.DareAndTruth.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "likes")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Like extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @NotNull
    UUID feedId;

    LocalDateTime likedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Like like = (Like) o;
        return Objects.equals(feedId, like.feedId)
                && Objects.equals(user, like.user)
                && Objects.equals(likedAt, like.likedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, feedId, likedAt);
    }
}

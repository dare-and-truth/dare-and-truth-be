package PNV.DareAndTruth.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user; // Người nhận lời mời kết bạn

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    @ToString.Exclude
    User follower; // Người gửi lời mời kết bạn

    @Column(name = "followed_at", nullable = false)
    LocalDateTime followedAt;

    @Column(name = "is_accepted", columnDefinition = "boolean default false")
    Boolean isAccepted;
}

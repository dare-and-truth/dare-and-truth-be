package PNV.DareAndTruth.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    User receiver;

    @Column(name = "type", nullable = false)
    String type;

    @Column(name = "content", columnDefinition = "TEXT")
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id")
    Reminder reminder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    Comment comment;

    @Column(name = "is_read", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Notification notification = (Notification) o;
        return Objects.equals(sender, notification.sender)
                && Objects.equals(receiver, notification.receiver)
                && Objects.equals(type, notification.type)
                && Objects.equals(post, notification.post)
                && Objects.equals(reminder, notification.reminder)
                && Objects.equals(isRead, notification.isRead)
                && Objects.equals(challenge, notification.challenge)
                && Objects.equals(comment, notification.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sender, receiver, type, post, reminder, isRead, challenge,comment);
    }
}

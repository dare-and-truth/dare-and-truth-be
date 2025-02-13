package PNV.DareAndTruth.entity;

import java.util.Objects;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends BaseEntityAudit {
    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "media_url", nullable = false)
    String mediaUrl;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Post post = (Post) o;
        return Objects.equals(content, post.content)
                && Objects.equals(mediaUrl, post.mediaUrl)
                && Objects.equals(isActive, post.isActive)
                && Objects.equals(isDeleted, post.isDeleted)
                && Objects.equals(user, post.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content, mediaUrl, isActive, isDeleted, user);
    }
}

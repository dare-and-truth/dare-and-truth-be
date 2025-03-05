package PNV.DareAndTruth.entity;

import java.util.*;

import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntityAudit {
    @Column(name = "username")
    String username;

    //    @Column(name = "avatar_url", nullable = false)
    //    String avatarUrl;

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "avatar_url")
    String avatarUrl;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "is_admin")
    Boolean isAdmin;

    public Collection<GrantedAuthority> getAuthorities() {
        return isAdmin != null && isAdmin
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isDeleted = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Challenge> challenges;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Post> posts;

    @Column(name = "refresh_token")
    String refreshToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Request> receivedRequests; // Requests received

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Request> sentRequests; // Requests sent

    // implement equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(username, that.username)
                && Objects.equals(email, that.email)
                && Objects.equals(password, that.password)
                && Objects.equals(isAdmin, that.isAdmin)
                && Objects.equals(isActive, that.isActive)
                && Objects.equals(isDeleted, that.isDeleted)
                && Objects.equals(challenges, that.challenges)
                && Objects.equals(posts, that.posts)
                && Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                username,
                email,
                password,
                isAdmin,
                isActive,
                isDeleted,
                challenges,
                posts,
                refreshToken);
    }
}

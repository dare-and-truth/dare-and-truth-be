package PNV.DareAndTruth.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

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

    @Column(name = "email", unique = true, nullable = false)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "is_admin")
    Boolean isAdmin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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

    @Column(name = "refresh_token")
    private String refreshToken;
}

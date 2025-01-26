package PNV.DareAndTruth.entity;

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

    @Column(name = "is_active", columnDefinition = "boolean default true")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    @Builder.Default
    Boolean isDeleted = false;
}

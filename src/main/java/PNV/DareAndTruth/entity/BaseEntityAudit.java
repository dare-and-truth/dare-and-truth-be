package PNV.DareAndTruth.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
    String createdBy;
    String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntityAudit that)) return false;
        if (!super.equals(o)) return false;
        return createdBy.equals(that.createdBy)
                && updatedBy.equals(that.updatedBy)
                && createdAt.equals(that.createdAt)
                && updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdBy, updatedBy, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BaseEntityAudit{" + "createdBy='"
                + createdBy + ", updatedBy='"
                + updatedBy + ", createdAt="
                + createdAt + ", updatedAt="
                + updatedAt + "}"
                + super.toString();
    }
}

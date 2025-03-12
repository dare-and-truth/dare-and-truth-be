package PNV.DareAndTruth.entity;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @Id
    ObjectId id;

    @Field(name = "participants")
    Set<UUID> participants;

    @Field(name = "last_message")
    MessagePreview lastMessage;

    @Field(name = "unread_counts")
    Map<UUID, Integer> unreadCounts;

    @LastModifiedDate
    @Field(name = "updated_at")
    Instant updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagePreview {
        String content;
        UUID senderId;
    }
}

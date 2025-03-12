package PNV.DareAndTruth.entity;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    ObjectId id;

    @Field(name = "conversation_id")
    ObjectId conversationId;

    @Field(name = "sender_id")
    UUID senderId;

    @Field(name = "content")
    String content;

    @CreatedDate
    @Field(name = "sent_at")
    Instant sentAt;

    @Field(name = "read_by")
    Set<String> readBy;
}
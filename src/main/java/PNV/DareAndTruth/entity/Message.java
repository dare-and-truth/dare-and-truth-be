package PNV.DareAndTruth.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Field(name = "media_url")
    String mediaUrl;

    @CreatedDate
    @Field(name = "sent_at")
    Instant sentAt;
}

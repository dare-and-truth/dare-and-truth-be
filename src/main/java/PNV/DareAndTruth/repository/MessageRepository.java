package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findByConversationId(ObjectId conversationId, Pageable pageable);

    // Lấy tin nhắn cũ hơn tin nhắn có `lastMessageId`
    @Query("{ 'conversationId': ?0, 'sentAt': { $lt: ?1 } }")
    List<Message> findByConversationIdAndSentAtLessThan(ObjectId conversationId, Instant lastSentAt, Pageable pageable);

    @Query("{ 'conversationId': ?0, 'readBy': { $ne: ?1 } }")
    List<Message> findUnreadMessages(ObjectId conversationId, UUID userId);
}

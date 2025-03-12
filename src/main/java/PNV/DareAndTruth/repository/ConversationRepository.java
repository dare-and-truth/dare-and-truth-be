package PNV.DareAndTruth.repository;

import PNV.DareAndTruth.entity.Conversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, ObjectId> {
    List<Conversation> findByParticipantsContainsOrderByUpdatedAtDesc(UUID userId);

    @Query("{'participants': {$all: ?0}}")
    Optional<Conversation> findByParticipants(Set<UUID> participants);

}

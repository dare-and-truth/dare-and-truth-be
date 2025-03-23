package PNV.DareAndTruth.service;

import java.util.*;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.MongoException;

import PNV.DareAndTruth.dto.request.message.SendMessageRequest;
import PNV.DareAndTruth.dto.response.message.MessageResponse;
import PNV.DareAndTruth.entity.Conversation;
import PNV.DareAndTruth.entity.Message;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ConversationRepository;
import PNV.DareAndTruth.repository.MessageRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    ConversationRepository conversationRepository;
    SimpMessagingTemplate messagingTemplate;
    UserRepository userRepository;

    @Transactional
    @Retryable(value = MongoException.class, maxAttempts = 5, backoff = @Backoff(delay = 300))
    public String sendMessage(UUID senderId, SendMessageRequest request) {

        if (request.getMediaUrl() == null && request.getContent() == null) {
            throw new AppException(ErrorCode.CONTENT_OR_MEDIA_URL_REQUIRE, HttpStatus.BAD_REQUEST);
        }

        UUID receiverId = request.getReceiverId();
        if (!userRepository.existsById(receiverId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Optional<User> senderOpt = userRepository.findById(senderId);
        User sender;

        if (senderOpt.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } else sender = senderOpt.get();

        ObjectId conversationId = request.getConversationId();
        Optional<Conversation> conversationOpt;

        if (conversationId != null) {
            conversationOpt = conversationRepository.findById(conversationId);
            if (conversationOpt.isEmpty()) {
                throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } else {
            conversationOpt = conversationRepository.findByParticipants(Set.of(senderId, receiverId));
        }

        Conversation conversation = conversationOpt.orElseGet(() -> {
            Conversation newConversation = new Conversation();
            newConversation.setParticipants(Set.of(senderId, receiverId));
            newConversation.setUnreadCounts(new HashMap<>());
            return conversationRepository.save(newConversation);
        });

        // Tạo tin nhắn
        Message message;
        message = Message.builder()
                .conversationId(conversation.getId())
                .content(request.getContent())
                .senderId(senderId)
                .build();
        if (request.getMediaUrl() != null) {
            message.setMediaUrl(request.getMediaUrl());
        }
        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }

        Message newMessage = messageRepository.save(message);

        MessageResponse messageResponse = MessageResponse.builder()
                .id(newMessage.getId().toString())
                .conversationId(newMessage.getConversationId().toString())
                .senderUsername(sender.getUsername())
                .senderAvatarUrl(sender.getAvatarUrl())
                .content(newMessage.getContent())
                .mediaUrl(newMessage.getMediaUrl())
                .sentAt(newMessage.getSentAt())
                .senderId(newMessage.getSenderId())
                .build();

        messagingTemplate.convertAndSend("/topic/messages/" + receiverId, messageResponse);

        // update Conversation
        conversation.setLastMessage(Conversation.MessagePreview.builder()
                .content(message.getContent())
                .senderId(message.getSenderId())
                .build());
        // Increment the number of unread messages of receiver
        conversation
                .getUnreadCounts()
                .put(receiverId, conversation.getUnreadCounts().getOrDefault(receiverId, 0) + 1);
        // Set read for the sender
        conversation.getUnreadCounts().put(senderId, 0);
        conversationRepository.save(conversation);
        return conversation.getId().toString();
    }
}

package PNV.DareAndTruth.service;

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
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {
    MessageRepository messageRepository;
    ConversationRepository conversationRepository;
    SimpMessagingTemplate messagingTemplate;
    UserRepository userRepository;

    @Transactional
    public void sendMessage(UUID senderId, SendMessageRequest request) {
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
            return conversationRepository.save(newConversation);
        });

        // Tạo tin nhắn
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .content(request.getContent())
                .senderId(senderId)
                .build();

        Message newMessage = messageRepository.save(message);

        MessageResponse messageResponse;

        if (conversationId != null) {
            messageResponse = MessageResponse.builder()
                    .id(newMessage.getId().toString())
                    .conversationId(conversationId.toString())
                    .content(newMessage.getContent())
                    .senderId(newMessage.getSenderId())
                    .sentAt(newMessage.getSentAt())
                    .build();
        } else {
            messageResponse = MessageResponse.builder()
                    .id(newMessage.getId().toString())
                    .conversationId(newMessage.getConversationId().toString())
                    .senderUsername(sender.getUsername())
                    .senderAvatarUrl(sender.getAvatarUrl())
                    .content(newMessage.getContent())
                    .sentAt(newMessage.getSentAt())
                    .senderId(newMessage.getSenderId())
                    .build();
        }

        messagingTemplate.convertAndSend(
                "/topic/messages/" + receiverId, messageResponse);


        // update Conversation
        if (conversation.getUnreadCounts() == null) {
            conversation.setUnreadCounts(new HashMap<>());
        }
        conversation.setLastMessage(Conversation.MessagePreview.builder()
                .content(message.getContent())
                .senderId(message.getSenderId())
                .build());
        // Increment the number of unread messages of receiver
        conversation.getUnreadCounts().put(receiverId, conversation.getUnreadCounts().getOrDefault(receiverId, 0) + 1);
        // Set read for the sender
        conversation.getUnreadCounts().put(senderId, 0);
        conversationRepository.save(conversation);
    }

    @Transactional
    public void markMessagesAsRead(ObjectId conversationId, UUID userId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isEmpty()) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Conversation conversation = conversationOpt.get();
        conversation.getUnreadCounts().put(userId, 0);
        conversationRepository.save(conversation);

        List<Message> unreadMessages = messageRepository.findUnreadMessages(conversationId, userId);

        if (unreadMessages.isEmpty()) return;

        unreadMessages.forEach(message -> {
            if (message.getReadBy() == null) {
                message.setReadBy(new HashSet<>()); // Khởi tạo nếu null
            }
            message.getReadBy().add(userId.toString());
        });
        messageRepository.saveAll(unreadMessages);
    }
}

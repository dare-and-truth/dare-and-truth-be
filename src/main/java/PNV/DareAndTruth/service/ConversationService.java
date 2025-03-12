package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.response.chat.ChatResponse;
import PNV.DareAndTruth.dto.response.chat.ConversationResponse;
import PNV.DareAndTruth.dto.response.chat.MessageResponse;
import PNV.DareAndTruth.dto.response.user.UserInfo;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    UserRepository userRepository;
    MessageRepository messageRepository;

    public List<ConversationResponse> getConversations(UUID userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(userId);

        // Lấy tất cả userId để batch query
        Set<UUID> allUserIds = conversations.stream()
                .flatMap(conv -> conv.getParticipants().stream())
                .collect(Collectors.toSet());

        // Batch query để lấy thông tin users
        Map<UUID, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return conversations.stream().map(conversation -> {
            // Map thông tin người dùng từ cache
            Set<UserInfo> participantInfos = conversation.getParticipants().stream()
                    .filter(userMap::containsKey)
                    .map(currentUserId -> {
                        User user = userMap.get(currentUserId);
                        return new UserInfo(user.getId(), user.getUsername(), user.getAvatarUrl());
                    })
                    .collect(Collectors.toSet());

            // Xử lý null safety
            Map<UUID, Integer> unreadCounts = conversation.getUnreadCounts();
            int unreadMessages = unreadCounts != null ? unreadCounts.getOrDefault(userId, 0) : 0;

            return new ConversationResponse(
                    conversation.getId().toString(),
                    participantInfos,
                    conversation.getLastMessage(),
                    unreadMessages,
                    conversation.getUpdatedAt()
            );
        }).toList(); // Dùng List thay vì Set để giữ thứ tự
    }

    public ChatResponse getChatBetweenUsers(UUID userId, String otherUserId, String conversationId, int limit, ObjectId lastMessageId) {
        UUID otherUserUUID = UUID.fromString(otherUserId);
        User otherUser = userRepository.findById(otherUserUUID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        Optional<Conversation> conversationOpt;
        if (conversationId == null) {
            conversationOpt = conversationRepository.findByParticipants(Set.of(userId, otherUserUUID));

            if (conversationOpt.isEmpty()) {
            // Just return user's information if no conversation exists
                UserInfo otherUserInfo = new UserInfo(otherUser.getId(), otherUser.getUsername(), otherUser.getAvatarUrl());
                return ChatResponse.builder().otherUser(otherUserInfo).build();
            }
        } else conversationOpt = conversationRepository.findById(new ObjectId(conversationId));

        if (conversationOpt.isEmpty()) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Conversation conversation = conversationOpt.get();

        // Get messages base on lastMessageId
        List<Message> messages;
        if (lastMessageId == null) {
            // Get the last messages
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sentAt"));
            messages = messageRepository.findByConversationId(conversation.getId(), pageable);
        } else {
            Optional<Message> lastMessageOpt = messageRepository.findById(lastMessageId);
            Instant lastSentAt = lastMessageOpt.orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND, HttpStatus.NOT_FOUND)).getSentAt();
            // Get messages sent after lastSentAt, sorted by sentAt in descending order
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sentAt"));
            messages = messageRepository.findByConversationIdAndSentAtLessThan(conversation.getId(), lastSentAt, pageable);
        }

        List<MessageResponse> messageResponses = messages.stream()
                .map(msg -> new MessageResponse(
                        msg.getId().toString(),
                        msg.getContent(),
                        msg.getSenderId(),
                        msg.getSentAt()
                ))
                .toList();

        ObjectId newLastMessageId = messages.get(messages.size() - 1).getId();

        if (lastMessageId == null) {
            // Lần request đầu tiên -> trả thêm conversationId & otherUser
            UserInfo otherUserInfo = new UserInfo(otherUser.getId(), otherUser.getUsername(), otherUser.getAvatarUrl());
            return ChatResponse.builder().conversationId(conversation.getId().toString()).otherUser(otherUserInfo).messages(messageResponses).lastMessageId(newLastMessageId.toString()).build();
        } else {
            // Các request tiếp theo -> chỉ trả messages & lastMessageId
            return ChatResponse.builder().messages(messageResponses).lastMessageId(newLastMessageId.toString()).build();
        }
    }
}

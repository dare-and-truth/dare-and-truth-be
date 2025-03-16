package PNV.DareAndTruth.service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import PNV.DareAndTruth.dto.response.chat.UnreadMessagesOfAllChatCountResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.response.chat.ChatResponse;
import PNV.DareAndTruth.dto.response.chat.ConversationResponse;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationService {
    ConversationRepository conversationRepository;
    UserRepository userRepository;
    MessageRepository messageRepository;

    public List<ConversationResponse> getConversations(UUID userId) {
        List<Conversation> conversations =
                conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(userId);

        // Lấy tất cả userId để batch query
        Set<UUID> allUserIds = conversations.stream()
                .flatMap(conv -> conv.getParticipants().stream())
                .collect(Collectors.toSet());

        // Batch query để lấy thông tin users
        Map<UUID, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return conversations.stream()
                .map(conversation -> {
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
                            conversation.getUpdatedAt());
                })
                .toList(); // Dùng List thay vì Set để giữ thứ tự
    }

    public ChatResponse getChatBetweenUsers(
            UUID userId, String otherUserId, String conversationId, int limit, ObjectId nextMessageId) {
        UUID otherUserUUID = UUID.fromString(otherUserId);
        User otherUser = userRepository
                .findById(otherUserUUID)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        Optional<Conversation> conversationOpt;
        if (conversationId == null) {
            conversationOpt = conversationRepository.findByParticipants(Set.of(userId, otherUserUUID));

            if (conversationOpt.isEmpty()) {
                // Just return user's information if no conversation exists
                UserInfo otherUserInfo =
                        new UserInfo(otherUser.getId(), otherUser.getUsername(), otherUser.getAvatarUrl());
                return ChatResponse.builder().otherUser(otherUserInfo).build();
            }
        } else {
            conversationOpt = conversationRepository.findById(new ObjectId(conversationId));
        }

        if (conversationOpt.isEmpty()) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Conversation conversation = conversationOpt.get();

        // Get messages based on nextMessageId
        List<Message> messages;
        ObjectId newNextMessageId = null; // Mặc định không có tin nhắn cũ hơn

        if (nextMessageId == null) {
            // Truy vấn lần đầu, lấy tin nhắn mới nhất
            Pageable pageable = PageRequest.of(0, limit + 1, Sort.by(Sort.Direction.DESC, "sentAt"));
            messages = messageRepository.findByConversationId(conversation.getId(), pageable);
        } else {
            // Tìm tin nhắn tương ứng với nextMessageId để lấy thời gian gửi
            Optional<Message> nextMessageOpt = messageRepository.findById(nextMessageId);
            Instant lastSentAt = nextMessageOpt
                    .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND, HttpStatus.NOT_FOUND))
                    .getSentAt();

            Pageable pageable = PageRequest.of(0, limit + 1, Sort.by(Sort.Direction.DESC, "sentAt"));
            messages =
                    messageRepository.findByConversationIdAndSentAtLessThan(conversation.getId(), lastSentAt, pageable);
        }

        // Kiểm tra xem có còn tin nhắn để tải không
        if (messages.size() > limit) {
            newNextMessageId = messages.get(limit).getId(); // ID của tin nhắn tiếp theo
            messages = messages.subList(0, limit); // Giữ lại số lượng đúng theo limit
        }

        List<ConversationResponse.MessageResponse> messageResponses = messages.stream()
                .map(msg -> new ConversationResponse.MessageResponse(
                        msg.getId().toString(), msg.getContent(), msg.getMediaUrl(), msg.getSenderId(), msg.getSentAt()))
                .toList();

        if (nextMessageId == null) {
            // Trả về thêm conversationId & thông tin người dùng cho request đầu tiên
            UserInfo otherUserInfo = new UserInfo(otherUser.getId(), otherUser.getUsername(), otherUser.getAvatarUrl());
            return ChatResponse.builder()
                    .conversationId(conversation.getId().toString())
                    .otherUser(otherUserInfo)
                    .messages(messageResponses)
                    .nextMessageId(newNextMessageId != null ? newNextMessageId.toString() : null)
                    .build();
        } else {
            // Các request tiếp theo -> chỉ trả messages & nextMessageId
            return ChatResponse.builder()
                    .messages(messageResponses)
                    .nextMessageId(newNextMessageId != null ? newNextMessageId.toString() : null)
                    .build();
        }
    }

    public UnreadMessagesOfAllChatCountResponse getTotalUnreadMessages(UUID userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContaining(userId);

        int unreadMessagesCount = conversations.stream()
                .mapToInt(conversation -> conversation.getUnreadCounts().getOrDefault(userId, 0))
                .sum();
        return UnreadMessagesOfAllChatCountResponse.builder().totalUnreadMessagesCount(unreadMessagesCount).build();
    }

    @Transactional
    public void markConversationAsRead(ObjectId conversationId, UUID userId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isEmpty()) {
            throw new AppException(ErrorCode.CONVERSATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Conversation conversation = conversationOpt.get();
        conversation.getUnreadCounts().put(userId, 0);
        conversationRepository.save(conversation);
    }
}

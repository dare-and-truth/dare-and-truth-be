package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import PNV.DareAndTruth.dto.response.notification.CommentNotificationResponse;
import PNV.DareAndTruth.dto.response.notification.FriendRequestNotificationResponse;
import PNV.DareAndTruth.entity.*;
import PNV.DareAndTruth.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    ChallengeRepository challengeRepository;
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

    public void createComment(CreateCommentRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        UUID feedId;
        Notification notification;
        CommentNotificationResponse commentNotificationResponse;

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (request.isChallenge()) {
            Optional<Challenge> challenge = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challenge.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else {
                feedId = challenge.get().getId();
                notification = Notification.builder().sender(user.get()).receiver(challenge.get().getUser()).challenge(challenge.get()).type("comment-challenge").build();
                commentNotificationResponse = CommentNotificationResponse.builder()
                        .type("comment-challenge")
                        .senderId(notification.getSender().getId())
                        .senderName(notification.getSender().getUsername())
                        .challengeId(feedId)
                        .hashtag(challenge.get().getHashtag())
                        .commentContent(request.getContent())
                        .build();
            }
        } else {
            Optional<Post> post = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (post.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else {
                feedId = post.get().getId();
                notification = Notification.builder().sender(user.get()).receiver(post.get().getUser()).post(post.get()).type("comment-post").build();
                commentNotificationResponse = CommentNotificationResponse.builder()
                        .type("comment-post")
                        .senderId(notification.getSender().getId())
                        .senderName(notification.getSender().getUsername())
                        .hashtag(post.get().getHashtag())
                        .commentContent(request.getContent())
                        .postId(feedId)
                        .build();
            }
        }

        notificationRepository.save(notification);

        // Gửi thông báo đến người nhận qua WebSocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + notification.getReceiver().getId(),
                commentNotificationResponse
        );

        var comment = Comment.builder()
                .user(user.get())
                .feedId(feedId)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .build();
        commentRepository.save(comment);
    }

    public Set<CommentSummaryProjection> getCommentsByFeedId(String feedId) {
        // handle invalid UUID
        UUID feedUUID;
        try {
            feedUUID = UUID.fromString(feedId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.FEED_ID_INVALID, HttpStatus.BAD_REQUEST);
        }
        // retrieve comments by feedId
        return commentRepository.findAllByFeedIdOrderByCreatedAtDesc(feedUUID);
    }
}

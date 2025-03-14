package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.dto.response.notification.CommentNotificationResponse;
import PNV.DareAndTruth.entity.*;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.*;
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
    ScoreRepository scoreRepository;
    ScoreService scoreService;

    public void createComment(CreateCommentRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        UUID feedId;
        Notification notification = null; // Initialize as null
        CommentNotificationResponse commentNotificationResponse = null; // Initialize as null

        User sender = user.get();

        String feedType;
        if(request.isChallenge()){
            feedType = "challenge";
        }else {
            feedType = "post";
        }

        if (request.isChallenge()) {
            Optional<Challenge> challenge = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challenge.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            feedId = challenge.get().getId();
            User receiver = challenge.get().getUser();

            if (sender.getId() != receiver.getId()) {
                notification = Notification.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .challenge(challenge.get())
                        .type("comment-challenge")
                        .content(request.getContent())
                        .build();
                commentNotificationResponse = CommentNotificationResponse.builder()
                        .type("comment-challenge")
                        .senderId(sender.getId())
                        .senderName(sender.getUsername())
                        .senderAvatarUrl(sender.getAvatarUrl())
                        .challengeId(feedId)
                        .hashtag(challenge.get().getHashtag())
                        .commentContent(request.getContent())
                        .build();
            }
        } else {
            Optional<Post> post = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (post.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }

            feedId = post.get().getId();
            User receiver = post.get().getUser();

            if (sender.getId() != receiver.getId()) {
                notification = Notification.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .post(post.get())
                        .type("comment-post")
                        .content(request.getContent())
                        .build();
                commentNotificationResponse = CommentNotificationResponse.builder()
                        .type("comment-post")
                        .senderId(sender.getId())
                        .senderName(sender.getUsername())
                        .senderAvatarUrl(sender.getAvatarUrl())
                        .postId(feedId)
                        .hashtag(post.get().getHashtag())
                        .commentContent(request.getContent())
                        .build();
            }
        }

        // Save notification and send via WebSocket if it exists
        if (notification != null) {
            notificationRepository.save(notification);
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notification.getReceiver().getId(), commentNotificationResponse);
        }

        // Create and save the comment
        Comment comment = Comment.builder()
                .user(sender)
                .feedId(feedId)
                .feedType(feedType)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .build();
        commentRepository.save(comment);

        scoreService.addCommentScore(feedId, feedType, userEmail);
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

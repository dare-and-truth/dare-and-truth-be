package PNV.DareAndTruth.service;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import PNV.DareAndTruth.dto.request.like.LikeRequest;
import PNV.DareAndTruth.dto.request.like.UnlikeRequest;
import PNV.DareAndTruth.dto.response.notification.LikeNotificationResponse;
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
public class LikeService {
    LikeRepository likeRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    ChallengeRepository challengeRepository;
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;
    ScoreRepository scoreRepository;
    ScoreService scoreService;

    public void likeFeed(LikeRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        UUID feedId;
        String feedType;

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Notification notification = null; // Initialize as null
        LikeNotificationResponse likeNotificationResponse = null; // Initialize as null
        User sender = user.get();

        // Check if user already liked this feed
        if (likeRepository.existsByUserIdAndFeedId(sender.getId(), UUID.fromString(request.getFeedId()))) {
            throw new AppException(ErrorCode.LIKE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        if (request.isChallenge()) {
            Optional<Challenge> challenge = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challenge.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else {
                feedId = challenge.get().getId();
                feedType = "challenge";
            }

            User receiver = challenge.get().getUser();

            if (sender.getId() != receiver.getId()) { // Only create notification if sender != receiver
                notification = Notification.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .challenge(challenge.get())
                        .type("like-challenge")
                        .build();
                likeNotificationResponse = LikeNotificationResponse.builder()
                        .type("like-challenge")
                        .senderId(sender.getId())
                        .senderName(sender.getUsername())
                        .challengeId(feedId) // Correct field name
                        .hashtag(challenge.get().getHashtag())
                        .build();
            }
        } else {
            Optional<Post> post = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (post.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else {
                feedId = post.get().getId();
                feedType = "post";
                User receiver = post.get().getUser();
                if (sender.getId() != receiver.getId()) { // Only create notification if sender != receiver
                    notification = Notification.builder()
                            .sender(sender)
                            .receiver(receiver)
                            .post(post.get())
                            .type("like-post")
                            .build();
                    likeNotificationResponse = LikeNotificationResponse.builder()
                            .type("like-post")
                            .senderId(sender.getId())
                            .senderName(sender.getUsername())
                            .postId(feedId) // Correct field name (was challengeId)
                            .hashtag(post.get().getHashtag())
                            .build();
                }
            }
        }

        Like like =
                Like.builder().feedId(feedId).feedType(feedType).user(sender).build();

        // Save like regardless of notification
        likeRepository.save(like);

        // Save and send notification if it exists
        if (notification != null) {
            notificationRepository.save(notification);
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + notification.getReceiver().getId(), likeNotificationResponse);
        }
        scoreService.addLikeScore(feedId, feedType, userEmail);
    }

    @Transactional
    public void unlikeFeed(UnlikeRequest request, String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        Optional<Like> existingLike =
                likeRepository.findByUserIdAndFeedId(user.get().getId(), UUID.fromString(request.getFeedId()));

        likeRepository.delete(
                existingLike.orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND, HttpStatus.BAD_REQUEST)));
    }
}

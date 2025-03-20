package PNV.DareAndTruth.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.dto.response.comment.CommentSummaryResponse;
import PNV.DareAndTruth.dto.response.notification.CommentNotificationResponse;
import PNV.DareAndTruth.dto.response.user.UserInfo;
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

        User sender = user.get();
        UUID feedId;
        String feedType = request.isChallenge() ? "challenge" : "post";
        Challenge challenge = null;
        Post post = null;

        // Xác định bài đăng hoặc thử thách
        if (request.isChallenge()) {
            Optional<Challenge> challengeOpt = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challengeOpt.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            challenge = challengeOpt.get();
            feedId = challenge.getId();
        } else {
            Optional<Post> postOpt = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (postOpt.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
            post = postOpt.get();
            feedId = post.getId();
        }

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository
                    .findById(UUID.fromString(request.getParentCommentId()))
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
        }

        // Tạo comment hoặc reply comment duy nhất
        Comment comment = Comment.builder()
                .user(sender)
                .feedId(feedId)
                .feedType(feedType)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .parentComment(parentComment)
                .level(request.getLevel())
                .build();
        commentRepository.save(comment);

        // Gửi thông báo nếu cần
        User receiver;
        if ((parentComment != null)) {
            receiver = parentComment.getUser();
        } else {
            if (challenge == null) receiver = post.getUser();
            else receiver = challenge.getUser();
        }

        if (!sender.getId().equals(receiver.getId())) {
            String notificationType;
            if ((parentComment != null)) {
                if ("challenge".equals(parentComment.getFeedType())) notificationType = "reply-comment-challenge";
                else notificationType = "reply-comment-post";
            } else {
                notificationType = (request.isChallenge() ? "comment-challenge" : "comment-post");
            }

            String hashtag = (challenge != null) ? challenge.getHashtag() : post.getHashtag();

            Notification notification = Notification.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .type(notificationType)
                    .content(request.getContent())
                    .comment(parentComment)
                    .post(post)
                    .challenge(challenge)
                    .build();
            notificationRepository.save(notification);

            CommentNotificationResponse commentNotificationResponse = CommentNotificationResponse.builder()
                    .type(notificationType)
                    .senderId(sender.getId())
                    .senderName(sender.getUsername())
                    .senderAvatarUrl(sender.getAvatarUrl())
                    .postId("reply-comment-post".equals(notificationType) ? feedId : null)
                    .challengeId("reply-comment-challenge".equals(notificationType) ? feedId : null)
                    .hashtag(hashtag)
                    .parentCommentId(parentComment != null ? parentComment.getId() : null)
                    .commentContent(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend("/topic/notifications/" + receiver.getId(), commentNotificationResponse);
        }

        // Cập nhật điểm số
        scoreService.addCommentScore(feedId, feedType, userEmail);
    }

    public List<CommentSummaryResponse> getCommentsByFeedId(String feedId, String feedUserId) {
        UUID feedUUID;
        UUID feedUserUUID;

        try {
            feedUUID = UUID.fromString(feedId);
            feedUserUUID = UUID.fromString(feedUserId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.FEED_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        // Lấy danh sách comment đã được sắp xếp
        List<CommentSummaryProjection> comments = new ArrayList<>(
                commentRepository.findAllByFeedIdAndParentCommentIsNullOrderByFeedUserFirst(feedUUID, feedUserUUID));
        Set<UUID> commentIds =
                comments.stream().map(CommentSummaryProjection::getId).collect(Collectors.toSet());

        // Đếm số lượng replies
        Map<UUID, Long> replyCounts = commentIds.isEmpty()
                ? Map.of()
                : commentRepository.countRepliesForComments(commentIds).stream()
                        .collect(Collectors.toMap(
                                row -> UUID.fromString(row[0].toString()), row -> Long.parseLong(row[1].toString())));

        // Chuyển đổi projection thành DTO
        return comments.stream()
                .map(comment -> new CommentSummaryResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getMediaUrl(),
                        comment.getCreatedAt(),
                        (comment.getUser() != null)
                                ? new UserInfo(
                                        comment.getUser().getId(),
                                        comment.getUser().getUsername(),
                                        comment.getUser().getAvatarUrl())
                                : null,
                        comment.getParentComment() != null
                                ? comment.getParentComment().getId().toString()
                                : null,
                        replyCounts.getOrDefault(comment.getId(), 0L).intValue(),
                        comment.getLevel()))
                .collect(Collectors.toList());
    }

    public List<CommentSummaryResponse> getRepliesByCommentId(String commentId) {
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.COMMENT_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        // Lấy danh sách replies
        List<CommentSummaryProjection> replies =
                commentRepository.findAllByParentComment_IdOrderByCreatedAtAsc(commentUUID);
        Set<UUID> replyIds =
                replies.stream().map(CommentSummaryProjection::getId).collect(Collectors.toSet());

        // Đếm số lượng replies con cho mỗi reply
        Map<UUID, Long> replyCounts;
        if (!replyIds.isEmpty()) {
            List<Object[]> replyData = commentRepository.countRepliesForComments(replyIds);
            replyCounts = replyData.stream()
                    .collect(Collectors.toMap(
                            row -> (UUID) row[0], // Ép kiểu UUID cho parentCommentId
                            row -> (Long) row[1] // Ép kiểu Long cho số lượng replies
                            ));
        } else {
            replyCounts = Map.of();
        }

        return replies.stream()
                .map(reply -> new CommentSummaryResponse(
                        reply.getId(),
                        reply.getContent(),
                        reply.getMediaUrl(),
                        reply.getCreatedAt(),
                        (reply.getUser() != null)
                                ? new UserInfo(
                                        reply.getUser().getId(),
                                        reply.getUser().getUsername(),
                                        reply.getUser().getAvatarUrl())
                                : null,
                        reply.getParentComment() != null
                                ? reply.getParentComment().getId().toString()
                                : null,
                        replyCounts.getOrDefault(reply.getId(), 0L).intValue(),
                        reply.getLevel()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(String commentId, String userEmail) {
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.COMMENT_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        Comment comment = commentRepository
                .findById(commentUUID)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void updateComment(String commentId, String newContent, String userEmail) {
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.COMMENT_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        Comment comment = commentRepository
                .findById(commentUUID)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.PERMISSION_DENIED, HttpStatus.BAD_REQUEST);
        }

        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    public CommentSummaryResponse getCommentById(String commentId) {
        UUID commentUUID;
        try {
            commentUUID = UUID.fromString(commentId);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.COMMENT_ID_INVALID, HttpStatus.BAD_REQUEST);
        }

        CommentSummaryProjection comment = commentRepository
                .findCommentById(commentUUID)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        return new CommentSummaryResponse(
                comment.getId(),
                comment.getContent(),
                comment.getMediaUrl(),
                comment.getCreatedAt(),
                (comment.getUser() != null)
                        ? new UserInfo(
                                comment.getUser().getId(),
                                comment.getUser().getUsername(),
                                comment.getUser().getAvatarUrl())
                        : null,
                comment.getParentComment() != null
                        ? comment.getParentComment().getId().toString()
                        : null,
                0,
                comment.getLevel());
    }
}

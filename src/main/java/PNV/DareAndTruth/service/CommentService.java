package PNV.DareAndTruth.service;

import PNV.DareAndTruth.dto.projection.comment.CommentSummaryProjection;
import PNV.DareAndTruth.dto.request.comment.CreateCommentRequest;
import PNV.DareAndTruth.entity.Challenge;
import PNV.DareAndTruth.entity.Comment;
import PNV.DareAndTruth.entity.Post;
import PNV.DareAndTruth.entity.User;
import PNV.DareAndTruth.exception.AppException;
import PNV.DareAndTruth.exception.ErrorCode;
import PNV.DareAndTruth.repository.ChallengeRepository;
import PNV.DareAndTruth.repository.CommentRepository;
import PNV.DareAndTruth.repository.PostRepository;
import PNV.DareAndTruth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    ChallengeRepository challengeRepository;

    public void createComment(CreateCommentRequest request, String userEmail){
        Optional<User> user = userRepository.findByEmail(userEmail);
        UUID feedId;

        if (user.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        if (request.isChallenge()) {
            Optional<Challenge> challenge = challengeRepository.findById(UUID.fromString(request.getFeedId()));
            if (challenge.isEmpty()) {
                throw new AppException(ErrorCode.CHALLENGE_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else feedId = challenge.get().getId();
        } else {
            Optional<Post> post = postRepository.findById(UUID.fromString(request.getFeedId()));
            if (post.isEmpty()) {
                throw new AppException(ErrorCode.POST_NOT_FOUND, HttpStatus.BAD_REQUEST);
            } else feedId = post.get().getId();
        }

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
